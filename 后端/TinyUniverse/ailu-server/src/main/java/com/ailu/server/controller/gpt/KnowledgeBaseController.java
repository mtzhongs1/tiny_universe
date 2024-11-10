package com.ailu.server.controller.gpt;

import com.ailu.context.BaseContext;
import com.ailu.context.IndexNameContext;
import com.ailu.entity.MyFile;
import com.ailu.exception.BaseException;
import com.ailu.result.Result;
import com.ailu.server.mapper.KnowledgeMapper;
import com.ailu.server.properties.RagProperties;
import com.ailu.util.UuidUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/14 下午12:31
 */
@RequestMapping("/knowledge")
@RestController
@Slf4j
public class KnowledgeBaseController{
    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Autowired
    private RagProperties ragProperties;

    public static final String[] POI_DOC_TYPES = {"doc", "docx", "ppt", "pptx", "xls", "xlsx"};

    @PostMapping("/save")
    public Result saveKnowledge(@RequestParam("doc") MultipartFile doc,@RequestParam("knowledgeId")String knowledgeId){
        // 对AI回答需要依据的数据进行预处理，如分词等
        String originalFilename = doc.getOriginalFilename();
        String uuid = knowledgeId;
        String fileName = StringUtils.cleanPath(originalFilename);
        String ext = getFileExtension(fileName);
        String pathName = "D:/RAG/" + uuid + "." + ext;

        // TODO:保存文档到本地
        try {
            doc.transferTo(new File(pathName));
        } catch (IOException e) {
            log.error("save to local error", e);
        }

        // TODO:转化文档格式
        Document document;
        document = transferExt(ext, pathName);

        // TODO:对文档内容进行分词
        Document docWithoutPath = new Document(document.text());
        // TODO:保存文件标识符，用来从数据库中获取文档属性
        docWithoutPath.metadata()
                .put("uuid", uuid);

        String segmentSize = "segmentSize", overlapSize = "overlapSize";
        // 计算文档中每个段的最大大小和段落之间的重叠区间大小
        Map<String, Integer> parameters = calculateOptimalParameters(docWithoutPath.text(),segmentSize,overlapSize);
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(
                parameters.get(segmentSize),parameters.get(overlapSize), new OpenAiTokenizer(GPT_3_5_TURBO));
        // TODO:构建嵌入存储导入器，用于处理文档段落的向量化和存储
        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(ragProperties.getEmbeddingModel())
                .embeddingStore(ragProperties.getEmbeddingStore())
                .build();
        // 向量化并存储到数据库
        embeddingStoreIngestor.ingest(docWithoutPath);
        // TODO:将文档相关信息(路径，扩展名，uuid，userId)存到数据库
        insertKnowledgeBaseItem(ext, uuid, pathName);
        return Result.success();
    }

    private void insertKnowledgeBaseItem(String ext, String uuid, String pathName) {
        MyFile file = new MyFile();
        file.setExt(ext);
        file.setUuid(uuid);
        file.setPath(pathName);
        file.setUserId(BaseContext.getCurrentId());
        knowledgeMapper.insert(file);
    }

    private static Document transferExt(String ext, String pathName) {
        Document document;
        if (ext.equalsIgnoreCase("txt")) {
            document = loadDocument(pathName, new TextDocumentParser());
        } else if (ext.equalsIgnoreCase("pdf")) {
            document = loadDocument(pathName, new ApachePdfBoxDocumentParser());
        } else if (ArrayUtils.contains(POI_DOC_TYPES, ext)) {
            document = loadDocument(pathName, new ApachePoiDocumentParser());
        } else {
            // 如果文件类型不支持解析，则记录警告信息并返回文件对象
            log.warn("该文件类型:{}无法解析，忽略", ext);
            throw new BaseException("不支持的文件类型");
        }
        return document;
    }
    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            // 文件名中没有后缀或者后缀位于文件名的末尾
            return "";
        } else {
            return fileName.substring(dotIndex + 1);
        }
    }
    private static Map<String, Integer> calculateOptimalParameters(String document,String segmentSizeKey,String overlapSizeKey) {
        int docLength = document.length();

        // 基础段落大小
        int baseSegmentSize = 100; // 假设基础段落大小为 100 字符

        // 根据文档长度调整段落大小
        int segmentSize = adjustSegmentSize(docLength, baseSegmentSize);

        // 计算重叠大小，假设为段落大小的 20%
        int overlapSize = calculateOverlapSize(segmentSize);

        // 返回结果
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put(segmentSizeKey, segmentSize);
        parameters.put(overlapSizeKey, overlapSize);

        return parameters;
    }

    /**
     * 根据文档长度调整段落大小。
     *
     * @param docLength 文档长度
     * @param baseSegmentSize 基础段落大小
     * @return 调整后的段落大小
     */
    private static int adjustSegmentSize(int docLength, int baseSegmentSize) {
        if (docLength < 500) {
            // 如果文档较短，保持基础段落大小
            return baseSegmentSize;
        } else if (docLength >= 500 && docLength < 2000) {
            // 如果文档中等长度，适当增加段落大小
            return baseSegmentSize * 2;
        } else {
            // 如果文档很长，增加段落大小
            return baseSegmentSize * 3;
        }
    }

    /**
     * 计算重叠大小。
     *
     * @param segmentSize 段落大小
     * @return 重叠大小
     */
    private static int calculateOverlapSize(int segmentSize) {
        return segmentSize / 5; // 假设重叠大小为段落大小的 20%
    }

}
