package com.ptit.edu.store.product.controller;

import com.ptit.edu.store.auth.dao.UserRespository;
import com.ptit.edu.store.constants.Constant;
import com.ptit.edu.store.customer.dao.CustomerRespository;
import com.ptit.edu.store.customer.dao.SaveClothesRepository;
import com.ptit.edu.store.product.dao.CategoryRepository;
import com.ptit.edu.store.product.dao.ProductsRepository;
import com.ptit.edu.store.product.dao.RateClothesRepository;
import com.ptit.edu.store.product.models.body.UpdateCategoryBody;
import com.ptit.edu.store.product.models.data.Category;
import com.ptit.edu.store.product.models.view.CategoryView;
import com.ptit.edu.store.response_model.OkResponse;
import com.ptit.edu.store.response_model.Response;
import com.ptit.edu.store.response_model.ServerErrorResponse;
import com.ptit.edu.store.utils.PageAndSortRequestBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
@Api(value = "candidate-api", description = "Nhóm API Category")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    private SaveClothesRepository saveClothesRepository;
    @Autowired
    private UserRespository userRespository;
    @Autowired
    private CustomerRespository customerRespository;
    @Autowired
    private RateClothesRepository rateClothesRepository;

    /**********************Category********************/
    @ApiOperation(value = "Lấy toàn bộ danh mục", response = Iterable.class)
    @GetMapping("/categories")
    public Response getAllClothes(
            @ApiParam(name = "pageIndex", value = "Index trang, mặc định là 0")
            @RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
            @ApiParam(name = "pageSize", value = "Kích thước trang, mặc đinh và tối đa là " + Constant.MAX_PAGE_SIZE)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(name = "sortBy", value = "Trường cần sort, mặc định là " + Category.TITLE)
            @RequestParam(value = "sortBy", defaultValue = Category.TITLE) String sortBy,
            @ApiParam(name = "sortType", value = "Nhận (asc | desc), mặc định là desc")
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType
    ) {
        Response response;

        try {
            Pageable pageable = PageAndSortRequestBuilder.createPageRequest(pageIndex, pageSize, sortBy, sortType, Constant.MAX_PAGE_SIZE);
            Page<CategoryView> categoryView = categoryRepository.getAllCategory(pageable);
            response = new OkResponse(categoryView);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @PostMapping("/addcategory")
    @ApiOperation(value = "api thêm 1 danh mục sản phẩm", response = Iterable.class)
    public Response insertCategory(String body) {
        Response response;
        try {
            Category category = new Category();
            category.setTitle(body);
            categoryRepository.save(category);
            response = new OkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }

        return response;
    }

    @PostMapping("/delcategory")
    @ApiOperation(value = "api xóa 1 danh mục sản phẩm", response = Iterable.class)
    public Response deleteCategory( String id) {
        Response response;
        try {
            categoryRepository.delete(id);
            response = new OkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }

        return response;
    }

    @PutMapping("/updcategory")
    @ApiOperation(value = "api sửa 1 danh mục sản phẩm", response = Iterable.class)
    public Response updateCategory(@RequestBody UpdateCategoryBody body) {
        Response response;
        try {
            Category category = categoryRepository.getOne(body.getId());
            category.setTitle(body.getTitle());
            categoryRepository.save(category);
            response = new OkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }




}
