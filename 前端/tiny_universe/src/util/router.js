
// TODO:路由跳转，并打开新的页面
export const newRoute = (url,router) => {
    const routerUrl = router.resolve({
        path: url,
    })
    window.open(routerUrl.href,'_blank');
}
