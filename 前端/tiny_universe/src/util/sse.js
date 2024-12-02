export function createEventSource(url,onerror) {
  const eventSource = new EventSource(url);

  eventSource.onopen = function () {
    console.log("连接成功");
  };

  eventSource.onerror = function (event) {
    if (event.eventPhase === EventSource.CLOSED) {
      console.log("关闭连接");
      eventSource.close();
    } else {
      // 这里也可以根据错误类型做出不同的处理
      console.error("发生错误", event);
      eventSource.close();
    }
    onerror();
  };

  return eventSource; // 返回 EventSource 实例，以便外部调用者可以对其进行进一步的操作或监听其他事件
}
