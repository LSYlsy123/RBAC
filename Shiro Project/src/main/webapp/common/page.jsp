<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script id="paginateTemplate" type="x-tmpl-mustache">
<div class="col-xs-6">
    <div class="dataTables_info" id="dynamic-table_info" role="status" aria-live="polite">
        总共 {{total}} 中的 {{from}} ~ {{to}}
    </div>
</div>
    
<div class="col-xs-6">
    <div class="dataTables_paginate paging_simple_numbers" id="dynamic-table_paginate">
        <ul class="pagination">
            <li class="paginate_button previous {{^firstUrl}}disabled{{/firstUrl}}" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-target="1" data-url="{{firstUrl}}" class="page-action">首页</a>
            </li>
            <li class="paginate_button {{^beforeUrl}}disabled{{/beforeUrl}}" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-target="{{beforePageNo}}" data-url="{{beforeUrl}}" class="page-action">前一页</a>
            </li>
            <li class="paginate_button active" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-id="{{pageNo}}" >第{{pageNo}}页</a>
                <input type="hidden" class="pageNo" value="{{pageNo}}" />
            </li>
            <li class="paginate_button {{^nextUrl}}disabled{{/nextUrl}}" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-target="{{nextPageNo}}" data-url="{{nextUrl}}" class="page-action">后一页</a>
            </li>
            <li class="paginate_button next {{^lastUrl}}disabled{{/lastUrl}}" aria-controls="dynamic-table" tabindex="0">
                <a href="#" data-target="{{maxPageNo}}" data-url="{{lastUrl}}" class="page-action">尾页</a>
            </li>
        </ul>
    </div>
</div>
</script>

<script type="text/javascript">
    //取出来
    var paginateTemplate=$("#paginateTemplate").html();
    Mustache.parse(paginateTemplate);

    //url:请求的链接,total:一共多少条数据,pageNo:当前多少页,pageSize:每页有多少条数据,currentSize:当前页面数据条数,idElement:页面元素,callback:方法

    function renderPage(url,total,pageNo,pageSize,currentSize,idElement,callback) {
        //最大页数
        var maxPageNo=Math.ceil(total/pageSize);
        //url以问号结尾&开始，否则？开始
        var paramStartChar=url.indexOf("?")>0 ? "&" : "?";
        //每页从第几条数据开始
        var from=(pageNo-1)*pageSize+1;
        var view={
            from:from>total ? total : from,
            to: to=(from+currentSize-1)>total ? total : (from+currentSize-1),
            total:total,
            pageNo:pageNo,
            maxPageNo:maxPageNo,
            nextPageNo:pageNo>=maxPageNo?maxPageNo:(pageNo+1),
            beforePageNo:pageNo==1?1:(pageNo+1),
            firstUrl:(pageNo==1) ? '':(url+paramStartChar+"pageNo=1&pageSize="+pageSize),
            beforeUrl:(pageNo==1) ? '':(url+paramStartChar+"PageNo="+(pageNo-1)+"&pageSize="+pageSize),
            nextUrl:(pageNo>=maxPageNo) ?'':(url+paramStartChar+"PageNo="+(pageNo+1)+"&pageSize="+pageSize),
            lastUrl:(pageNo>=maxPageNo) ?'':(url+paramStartChar+"PageNo="+maxPageNo+"&pageSize="+pageSize)
        };

        $("#"+idElement).html(Mustache.render(paginateTemplate,view));

        $(".page-action").click(function (e) {
            e.preventDefault();
            $("#"+idElement+" .pageNo").val($(this).attr("data-target"));
            var targetUrl=$(this).attr("data-url");
            if (targetUrl!=''){
                $.ajax({
                    url:targetUrl,
                    success:function (result) {
                        if (callback){
                            callback(result,url);
                        }
                    }
                });
            }
        });
    }
</script>