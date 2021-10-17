package com.ptit.edu.store.auth.controller;

import com.ptit.edu.store.admin.dao.AdminRepository;
import com.ptit.edu.store.admin.models.Admin;
import com.ptit.edu.store.admin.models.body.AdminRegisterBody;
import com.ptit.edu.store.auth.dao.UserRespository;
import com.ptit.edu.store.auth.models.User;
import com.ptit.edu.store.auth.models.body.CustomerRegisterBody;
import com.ptit.edu.store.auth.models.body.NewPassword;
import com.ptit.edu.store.customer.dao.CustomerRespository;
import com.ptit.edu.store.customer.models.data.Customer;
import com.ptit.edu.store.customer.models.view.HeaderProfile;
import com.ptit.edu.store.response_model.*;
import com.ptit.edu.store.utils.EmailValidate;
import com.ptit.edu.store.utils.PasswordValidate;
import com.ptit.edu.store.utils.SendEmailUtils;
import com.ptit.edu.store.utils.UserDecodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auths")
@Api(value = "auth-api", description = "Nhóm API đăng nhập và cấp access token, Không yêu cầu access token")
public class AuthController {
    //    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    UserRespository userRespository;
    @Autowired
    CustomerRespository customerRespository;
    @Autowired
    AdminRepository adminRepository;
    @ApiOperation(value = "api đăng nhập cho khách hàng", response = Iterable.class)
    @PostMapping("/customer/login")
    public Response CustomerLogin(@ApiParam(name = "Authorization", value = "username+\":\"+password, lấy kết quả encode theo Base64, sau đó thêm \"Basic \" + kết quả")
                                  @RequestHeader(HeaderConstant.AUTHORIZATION) String encodedString,
                                  @RequestBody String fcmToken) {
        Response response;
        try {
            User user = UserDecodeUtils.decodeFromAuthorizationHeader(encodedString);
            if (userRespository.findByUsername(user.getUsername()) == null) {
                return new NotFoundResponse("Account not exist");
            }
            if (!userRespository.isAccountActivated(user.getUsername(), RoleConstants.CUSTOMER)) {
                response = new ForbiddenResponse(ResponseConstant.ErrorMessage.ACCOUNT_NOT_VERIFIED);
            } else {
                User login = userRespository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
                if (login != null) {
                    Customer customer = customerRespository.findByUser_Id(login.getId());
                    HeaderProfile profile = new HeaderProfile(customer.getId(), customer.getFullName(),
                            customer.getAvatarUrl(), customer.getEmail());
                    login.setFcmToken(fcmToken);
                    userRespository.save(login);
                    response = new OkResponse(profile);
                } else {
                    response = new Response(HttpStatus.UNAUTHORIZED, ResponseConstant.Vi.WRONG_EMAIL_OR_PASSWORD);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }
    @ApiOperation(value = "Api đăng nhập cho admin", response = Iterable.class)
    @PostMapping("/admin/login")
    public Response AdminLogin(@ApiParam(name = "encodedString", value = "username+\":\"+password, lấy kết quả encode theo Base64, sau đó thêm \"Basic \" + kết quả")
                               @RequestHeader(HeaderConstant.AUTHORIZATION) String encodedString) {
        Response response;
        try {
            User user = UserDecodeUtils.decodeFromAuthorizationHeader(encodedString);
            if (!userRespository.isAccountActivated(user.getUsername(), RoleConstants.ADMIN)) {
                response = new ForbiddenResponse(ResponseConstant.ErrorMessage.ACCOUNT_NOT_VERIFIED);
            } else {
                User login = userRespository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
                if (login != null) {
                    response = new OkResponse();
                } else {
                    response = new Response(HttpStatus.UNAUTHORIZED, ResponseConstant.Vi.WRONG_EMAIL_OR_PASSWORD);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }



    @ApiOperation(value = "Đăng ký tài khoản khach hang", response = Iterable.class)
    @PostMapping("/customers/register")
    public Response register(@ApiParam(name = HeaderConstant.AUTHORIZATION,
            value = "username+\":\"+password, lấy kết quả encode theo Base64, sau đó thêm \"Basic \" + kết quả", required = true)
                             @RequestHeader(value = HeaderConstant.AUTHORIZATION) String encodedString, @ApiParam(name = "customerRegisterBody", value = "Tên đầy đủ KH", required = true)
                             @Valid @RequestBody CustomerRegisterBody customerRegisterBody) {
        Response response;
        try {
            User u = UserDecodeUtils.decodeFromAuthorizationHeader(encodedString);
            if (!PasswordValidate.isPasswordValidate(u.getPassword())) {
                return new ForbiddenResponse(ResponseConstant.ErrorMessage.PASSWORD_TOO_SHORT);
            }

            User user = userRespository.findByUsername(u.getUsername());
            if (user != null) {
                return new ResourceExistResponse("Tai khoan da ton tai!");
            } else {
                user = new User();
                user.setPassword(u.getPassword());
                user.setUsername(u.getUsername());
                user.setRole(RoleConstants.CUSTOMER);
                user.setActived(true);

                userRespository.save(user);

                Customer customer = new Customer();
                customer.setUser(user);
                customer.setFullName(customerRegisterBody.getFullName());
                customer.setAddress(customerRegisterBody.getAddress());
                customer.setPhone(customerRegisterBody.getPhone());
                customer.setEmail(customerRegisterBody.getEmail());
                customerRespository.save(customer);

                SendEmailUtils.sendEmailActiveAccount(u.getUsername());
                response = new OkResponse(u.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "Đăng ký tài khoản admin", response = Iterable.class)
    @PostMapping("/admins/register")
    public Response adminRegister(@ApiParam(name = HeaderConstant.AUTHORIZATION, value = "username+\":\"+password, lấy kết quả encode theo Base64, sau đó thêm \"Basic \" + kết quả", required = true)
                                  @RequestHeader(value = HeaderConstant.AUTHORIZATION) String encodedString, @ApiParam(name = "adminRegisterBody", value = "Tên đầy đủ KH", required = true)
                                  @Valid @RequestBody AdminRegisterBody adminRegisterBody) {
        Response response;
        try {
            User u = UserDecodeUtils.decodeFromAuthorizationHeader(encodedString);
            if (!PasswordValidate.isPasswordValidate(u.getPassword())) {
                return new ForbiddenResponse(ResponseConstant.ErrorMessage.PASSWORD_TOO_SHORT);
            }

            User user = userRespository.findByUsername(u.getUsername());
            if (user != null) {
                return new ResourceExistResponse("Tai khoan da ton tai!");
            } else {
                user = new User();
                user.setPassword(u.getPassword());
                user.setUsername(u.getUsername());
                user.setRole(RoleConstants.ADMIN);
                user.setActived(true);

                userRespository.save(user);

                Admin admin = new Admin();
                admin.setFullName(adminRegisterBody.getFullName());
                admin.setPosition(adminRegisterBody.getPosition());

                adminRepository.save(admin);

//                SendEmailUtils.sendEmailrequest(u.getUsername());
                response = new OkResponse();
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "Xac nhan email", response = Iterable.class)
    @GetMapping("/registration/confirm/{username}")
    public Response confirm_email(@PathVariable("username") String username) {
        Response response;
        try {
            username += ".com";
            if (userRespository.findByUsername(username) == null) {
                return new NotFoundResponse("Email khong ton tai !");
            }
            userRespository.activeAccount(true, username);
            response = new OkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "Xac nhan email", response = Iterable.class)
    @PostMapping("/resend/registration/confirm/{username}")
    public Response resend_confirm_email(@PathVariable("username") String username) {
        Response response;
        try {
            username += ".com";
            SendEmailUtils.sendEmailActiveAccount(username);
            response = new OkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "Đổi mật khẩu", response = Iterable.class)
    @PostMapping("/customer/{customerID}/newPassword")
    public Response changePassword(@PathVariable("customerID") String customerID,
                                   @Valid @RequestBody NewPassword password) {
        Response response;
        try {
            Customer customer = customerRespository.findOne(customerID);
            if (customer == null) {
                return new NotFoundResponse("Customer not Exist");
            }
            User u = customer.getUser();
            if (u.getPassword().matches(password.getOldPassword())) {
                u.setPassword(password.getNewPassword());
                userRespository.save(u);
                response = new OkResponse();
            } else {
                response = new Response(HttpStatus.CONFLICT, ResponseConstant.Vi.OLD_PASSWORD_MISMATCH);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

    @ApiOperation(value = "Quên mật khẩu (Gửi lại email reset mật khẩu)", response = Iterable.class)
    @PostMapping("/customer/{id}/reset_password")
    public Response sendEmailToRessetPassword(
            @PathVariable("id") String customerID,
            @Valid @RequestBody String email) {
        Response response;
        try {
            Customer customer = customerRespository.findOne(customerID);
            if (customer == null) {
                return new NotFoundResponse("Customer not Exist");
            }
            if (!EmailValidate.validate(email)) {
                return new Response(HttpStatus.GONE, ResponseConstant.ErrorMessage.INVALID_EMAIL);
            }

            User u = customer.getUser();
            RandomString randomString = new RandomString();
            String resetPassword = randomString.nextString();
            u.setPassword(resetPassword);
            userRespository.save(u);
            SendEmailUtils.sendEmailResetPassword(email, resetPassword);
            response = new OkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            response = new ServerErrorResponse();
        }
        return response;
    }

}
