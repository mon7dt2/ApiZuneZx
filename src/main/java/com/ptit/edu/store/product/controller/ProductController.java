package com.ptit.edu.store.product.controller;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.StorageException;
import com.google.firebase.cloud.StorageClient;
import com.ptit.edu.store.auth.dao.UserRespository;
import com.ptit.edu.store.constants.Constant;
import com.ptit.edu.store.customer.dao.CustomerRespository;
import com.ptit.edu.store.customer.dao.SaveClothesRepository;
import com.ptit.edu.store.customer.models.data.Customer;
import com.ptit.edu.store.product.dao.*;
import com.ptit.edu.store.product.models.body.ClothesBody;
import com.ptit.edu.store.product.models.body.RateClothesBody;
import com.ptit.edu.store.product.models.data.*;
import com.ptit.edu.store.product.models.view.ProductPreview;
import com.ptit.edu.store.product.models.view.ProductViewModel;
import com.ptit.edu.store.product.models.view.RateClothesViewModel;
import com.ptit.edu.store.product.models.view.ThongKeView;
import com.ptit.edu.store.response_model.*;
import com.ptit.edu.store.utils.PageAndSortRequestBuilder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javafx.scene.shape.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Api(value = "candidate-api", description = "Nhóm API Product")
@CrossOrigin(origins = "*")
public class ProductController {

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
    @Autowired
    private OrderRepository orderRepository;

//    @Value("${file.upload-dir}")
    String FILE_DIRECTORY = "D:\\AndroidProjects-4.2\\Zunezx\\API\\store\\ptit.store\\src\\main\\resources\\static\\uploadImages";

    /**********************Clothes********************/
    @ApiOperation(value = "Lấy toàn bộ sản phẩm quần áo", response = Iterable.class)
    @GetMapping("/categorys/0")
    public Response getAllClothes(
            @ApiParam(name = "pageIndex", value = "Index trang, mặc định là 0")
            @RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
            @ApiParam(name = "pageSize", value = "Kích thước trang, mặc đinh và tối đa là " + Constant.MAX_PAGE_SIZE)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(name = "sortBy", value = "Trường cần sort, mặc định là " + Product.CREATED_DATE)
            @RequestParam(value = "sortBy", defaultValue = Product.CREATED_DATE) String sortBy,
            @ApiParam(name = "sortType", value = "Nhận (asc | desc), mặc định là desc")
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType
    ) {
        Response response;

        try {
            Pageable pageable = PageAndSortRequestBuilder.createPageRequest(pageIndex, pageSize, sortBy, sortType, Constant.MAX_PAGE_SIZE);
            Page<ProductPreview> clothesPreviews = productsRepository.getAllClothesPreviews(pageable);
            response = new OkResponse(clothesPreviews);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    /**********************detailClothes********************/
    @ApiOperation(value = "Lấy chi tiết sản phẩm", response = Iterable.class)
    @GetMapping("/{customerID}/clothes/{id}")
    public Response getDetailClothes(@PathVariable("customerID") String customerID,
                                     @PathVariable("id") String clothesID) {
        Response response;
        try {
            Customer customer = customerRespository.findOne(customerID);
            if (customer == null) {
                return new NotFoundResponse("Customer not Exist");
            }
            Product product = productsRepository.findById(clothesID);
            if (product == null) {
                return new NotFoundResponse("Clothes not Exist");
            }
            ProductViewModel clothesViewModel = productsRepository.getClothesViewModel(clothesID);
            clothesViewModel.setIsSaved(saveClothesRepository.existsByCustomer_IdAndProduct_Id(customerID, clothesID));
            response = new OkResponse(clothesViewModel);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }
    @ApiOperation(value = "Lấy chi tiết sản phẩm", response = Iterable.class)
    @GetMapping("clothes/{id}")
    public Response getDetailClothesWithoutAuth(
            @PathVariable("id") String clothesID) {
        Response response;
        try {
            Product product = productsRepository.findById(clothesID);
            if (product == null) {
                return new NotFoundResponse("Clothes not Exist");
            }
            ProductViewModel clothesViewModel = productsRepository.getClothesViewModel(clothesID);
            response = new OkResponse(clothesViewModel);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "api Thêm mới sản phẩm quần áo", response = Iterable.class)
    @RequestMapping(path = "/clothes/{id}", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Response insertClothes(@PathVariable("id") String categoryID,
                                  @RequestParam(value = "name",required = true) String name,
                                  @RequestParam(value = "description",required = true) String description,
                                  @RequestParam(value = "price",required = true) double price,
                                  @RequestParam(value = "size",required = true) String size,
                                  @RequestParam(value = "quantity",required = true) int quantity,
                                  @RequestParam(value = "avatar",required = true) MultipartFile avatar,
                                  @RequestParam(value = "cover",required = true) MultipartFile cover) {
        Category category = categoryRepository.findOne(categoryID);
        if (category == null) {
            return new NotFoundResponse("Category not Exist");
        }

        Product product = new Product(name, price, description, size, quantity);
        product.setCategory(category);
        productsRepository.save(product);
        if(avatar != null && cover != null){
            try {
                String productID = productsRepository.getProductByName(name);
                String avatarUrl = uploadFile("products/" + productID, productID + "_avatar.jpg",
                        avatar.getBytes(), "image/jpeg");
                String coverUrl = uploadFile("products/" + productID, productID + "_cover.jpg",
                        cover.getBytes(), "image/jpeg");
                Product product1 = productsRepository.getOne(productID);
                product1.setAvatarUrl(avatarUrl);
                product1.setCoverUrl(coverUrl);
                productsRepository.save(product1);
            }catch (IOException e) {
                e.printStackTrace();
                return new ResourceExistResponse("tên sản phẩm đã tồn tại");
            }
        }

        return new OkResponse();
    }

    public static String uploadFile(String dir, String fileName,
                                    byte[] data,
                                    String contentType) throws StorageException {
        Blob avatarFile = StorageClient.getInstance()
                .bucket()
                .create(dir+"/"+fileName, data, contentType);
        return getDownloadUrl(avatarFile.getBucket(), avatarFile.getName());
    }

    public static String getDownloadUrl(String bucketUrl, String fileName) {
        return "http://storage.googleapis.com/" + bucketUrl + "/" + fileName;
    }

    @ApiOperation(value = "api update sản phẩm", response = Iterable.class)
    @RequestMapping(path = "/updateclothes/{productID}", method = RequestMethod.PUT, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Response updateClothes(@PathVariable("productID") String productID,
              @RequestParam(value = "name",required = false) String name,
              @RequestParam(value = "description",required = false) String description,
              @RequestParam(value = "price",required = false) double price,
              @RequestParam(value = "size",required = false) String size,
              @RequestParam(value = "quantity",required = false) int quantity,
              @RequestParam(value = "avatar",required = false) MultipartFile avatar,
              @RequestParam(value = "cover",required = false) MultipartFile cover) {
        Product product = productsRepository.findOne(productID);
        if (product == null) {
            return new NotFoundResponse("Clothes not Exist");
        }
        try {
            if (name != null) product.setName(name);
            if (description != null) product.setDescription(description);
            if (price != -1) product.setPrice(price);
            if (quantity != -1) product.setQuantity(quantity);
            if (size != null) product.setSize(size);
            if (avatar != null){
                String avatarUrl = uploadFile("products/" + productID, productID + "_avatar.jpg",
                        avatar.getBytes(), "image/jpeg");
                product.setAvatarUrl(avatarUrl);
            }
            if (cover != null){
                String coverUrl = uploadFile("products/" + productID, productID + "_cover.jpg",
                        cover.getBytes(), "image/jpeg");
                product.setCoverUrl(coverUrl);
            }
            productsRepository.save(product);
        } catch (IOException e){
            e.printStackTrace();
            return new ServerErrorResponse();
        }

        return new OkResponse();
    }

    @ApiOperation(value = "api xóa sản phẩm như cách tôi làm lại cuộc đời", response = Iterable.class)
    @PostMapping("/deleteclothes/")
    public Response deleteClothes(String id) {
        Response response;
        try {
            productsRepository.delete(id);
            response = new OkResponse();
        } catch (Exception e){
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "api update sale", response = Iterable.class)
    @PutMapping("/updatesale/{id}")
    public Response updateSale(@PathVariable("id") String id,
                               float salePercent) {
        Response response;
        try {
            Product p = productsRepository.getOne(id);
            p.setIsSale(1);
            p.setSalePercent(salePercent);
            productsRepository.save(p);
            response = new OkResponse();
        } catch (Exception e){
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "api update sale", response = Iterable.class)
    @PutMapping("/removesale/{id}")
    public Response removeSale(@PathVariable("id") String id) {
        Response response;
        try {
            Product p = productsRepository.getOne(id);
            p.setIsSale(0);
            p.setSalePercent(0);
            productsRepository.save(p);
            response = new OkResponse();
        } catch (Exception e){
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "api search product", response = Iterable.class)
    @PostMapping("/countsubcate/")
    public Response countBySubCate(String des) {
        Response response;
        try {
            Long x = productsRepository.countByDescription(des);
            response = new OkResponse(x);
        } catch (Exception e){
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "api Thống kê", response = Iterable.class)
    @GetMapping("/statistic/")
    public Response Statistic () {
        Response response;
        try {
            List<Object[]> data = productsRepository.countBySubCate();
            Map<String, BigDecimal> map = null;
            if (data != null && !data.isEmpty()){
                map = new HashMap<>();
                for (Object[] object: data) {
                    map.put((String) object[0], (BigDecimal) object[1]);
                }
            }

            long totalProduct = productsRepository.count();
            long totalOrder = orderRepository.count();
            long orderChecked = orderRepository.countByIsCheck(1);
            long orderUnchecked = orderRepository.countByIsCheck(0);
            Double totalIncome = orderRepository.totalIncome();
            ThongKeView view = new ThongKeView(totalProduct, totalIncome, totalOrder, orderChecked, orderUnchecked, map);
            response = new OkResponse(view);
        } catch (Exception e){
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    /**********************similarClothes********************/
    @ApiOperation(value = "Lấy danh sách quần áo tưởng đương", response = Iterable.class)
    @GetMapping("/similarClothes/{id}")
    public Response getSimilarClothes(@PathVariable("id") String clothesID,
                                      @ApiParam(name = "pageIndex", value = "index trang, mặc định là 0")
                                      @RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
                                      @ApiParam(name = "pageSize", value = "Kích thước trang, mặc định và tối đa là " + Constant.MAX_PAGE_SIZE)
                                      @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                      @ApiParam(name = "sortBy", value = "Trường cần sort, mặc định là " + Product.CREATED_DATE)
                                      @RequestParam(value = "sortBy", defaultValue = Product.CREATED_DATE) String sortBy,
                                      @ApiParam(name = "sortType", value = "Nhận (asc | desc), mặc định là desc")
                                      @RequestParam(value = "sortType", defaultValue = "desc") String sortType) {
        Response response;
        try {
            Product product = productsRepository.findOne(clothesID);
            if (product == null) {
                return new NotFoundResponse("Clothes not Exist");
            }
            Pageable pageable = PageAndSortRequestBuilder.createPageRequest(pageIndex, pageSize, sortBy, sortType, Constant.MAX_PAGE_SIZE);
            Page<ProductPreview> clothesPreviews = productsRepository.getSimilarClothesPreviews(pageable, product.getCategory().getId());
            response = new OkResponse(clothesPreviews);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @GetMapping("/categorys/{id}")
    public Response getProductBySubCate(
            @ApiParam(name = "pageIndex", value = "Index trang, mặc định là 0")
            @RequestParam(value = "pageIndex", defaultValue = "0") Integer pageIndex,
            @ApiParam(name = "pageSize", value = "Kích thước trang, mặc đinh và tối đa là " + Constant.MAX_PAGE_SIZE)
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @ApiParam(name = "sortBy", value = "Trường cần sort, mặc định là " + Product.CREATED_DATE)
            @RequestParam(value = "sortBy", defaultValue = Product.CREATED_DATE) String sortBy,
            @ApiParam(name = "sortType", value = "Nhận (asc | desc), mặc định là desc")
            @RequestParam(value = "sortType", defaultValue = "desc") String sortType,
            @PathVariable("id") String id
    ) {
        Response response;

        try {
            Pageable pageable = PageAndSortRequestBuilder.createPageRequest(pageIndex, pageSize, sortBy, sortType, Constant.MAX_PAGE_SIZE);
            Page<ProductPreview> clothesPreviews = productsRepository.getProductByCategory(pageable, id);
            response = new OkResponse(clothesPreviews);
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    /**********************SaveClothes********************/

    @ApiOperation(value = "api Lưu sản phẩm", response = Iterable.class)
    @PostMapping("/{customerID}/saveClothes/{id}")
    public Response saveClothes(@PathVariable("customerID") String customerID, @PathVariable("id") String clothesID) {
        Response response;
        try {
            Customer customer = customerRespository.findOne(customerID);
            if (customer == null) {
                return new NotFoundResponse();
            }
            Product product = productsRepository.findById(clothesID);
            if (product == null) {
                return new NotFoundResponse("Clothes not Exist");
            }
            if (saveClothesRepository.existsByCustomer_IdAndProduct_Id(customerID, clothesID)) {
                return new ResourceExistResponse();
            }
            product.addSave();
            saveClothesRepository.save(new SaveClothes(product, customer));
            response = new OkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }
    @ApiOperation(value = "Api hủy lưu sản phẩm", response = Iterable.class)
    @DeleteMapping("/{customerID}/saveClothes/{id}/")
    public Response deleteSaveClothes(@PathVariable("customerID") String customerID, @PathVariable("id") String clothesID) {
        Response response;
        try {

            Customer customer = customerRespository.findOne(customerID);
            if (customer == null) {
                return new NotFoundResponse();
            }
            Product product = productsRepository.findOne(clothesID);
            if (product == null) {
                return new NotFoundResponse("Clothes not Exist");
            }
            if (!saveClothesRepository.existsByCustomer_IdAndProduct_Id(customerID, clothesID)) {
                return new ResourceExistResponse();
            }

            product.subSave();
            productsRepository.save(product);
            saveClothesRepository.deleteByCustomer_idAndProduct_Id(clothesID, customer.getId());
            response = new OkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;

    }

    /**********************Clothes********************/

    @ApiOperation(value = "Lấy tất cả danh mục sản phẩm", response = Iterable.class)
    @GetMapping("/category")
    public Response getAllCategory() {
        return new OkResponse(categoryRepository.findAll());
    }
//    private User getAuthenticatedUser() {
//        String userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        return userRespository.findByUsername(userEmail);
//    }
}
