package site.iblogs.portal.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import site.iblogs.common.api.ApiResponse;
import site.iblogs.common.api.PageResponse;
import site.iblogs.model.Contents;
import site.iblogs.portal.model.params.MetaDataType;
import site.iblogs.portal.model.request.content.ContentMetaPageRequest;
import site.iblogs.portal.model.response.ArchivesResponse;
import site.iblogs.portal.model.response.ContentResponse;
import site.iblogs.portal.service.ContentService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Date;
import java.util.List;

/**
 * 内容管理Controller
 *
 * @author Liu Zhenyu on 3/12/2020
 */
@Api(tags = "ContentController", value = "内容管理")
@Controller
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentController.class);

    @ApiOperation("获取所有内容")
    @RequestMapping(value = "all", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<List<Contents>> listContent() {
        return ApiResponse.success(contentService.listAllContent());
    }

    @ApiOperation("分页获取所有内容")
    @RequestMapping(value = "page", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<PageResponse<ContentResponse>> pagedContent(int pageNum, int pageSize, @RequestParam(defaultValue = "index") String orderType) {
        return ApiResponse.success(contentService.listContent(pageNum, pageSize, orderType, true));
    }

    @ApiOperation("通过链接或者ID获取文章")
    @RequestMapping(value = "index", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<ContentResponse> index(String url) {
        ContentResponse response = contentService.getByUrl(url);
        return response == null ? ApiResponse.failed() : ApiResponse.success(response);
    }

    @ApiOperation("通过标签获取文章")
    @RequestMapping(value = "tag", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse<PageResponse<ContentResponse>> tag(@RequestBody ContentMetaPageRequest request) {
        return ApiResponse.success(contentService.getContentByMetaData(MetaDataType.Tag.ordinal(), request.getMeta(), request.getPageNum(), request.getPageSize()));
    }

    @ApiOperation("通过通过分类获取文章")
    @RequestMapping(value = "category", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse<PageResponse<ContentResponse>> category(@RequestBody ContentMetaPageRequest request) {
        return ApiResponse.success(contentService.getContentByMetaData(MetaDataType.Category.ordinal(), request.getMeta(), request.getPageNum(), request.getPageSize()));
    }

    @ApiOperation("文章归档")
    @RequestMapping(value = "archives", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<List<ArchivesResponse>> archives() {
        return ApiResponse.success(contentService.contentArchives());
    }

    @ApiOperation("通过归档日期获取文档")
    @RequestMapping(value = "byArchive", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<PageResponse<ContentResponse>> ByArchive(Date archive, int pageNum, int pageSize) {
        return ApiResponse.success(contentService.getContentByArchive(archive, pageNum, pageSize));
    }

    @ApiOperation("文章查找(目前支持对标题查找)")
    @RequestMapping(value = "search", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse<PageResponse<ContentResponse>> Search(String keyword, int pageNum, int pageSize) {
        throw new NotImplementedException();
    }
}
