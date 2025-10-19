package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端地址簿控制器
 * 负责处理用户地址相关的HTTP请求
 */
@RestController
@RequestMapping("/user/addressBook")
@Tag(name = "C端-地址簿接口")
public class AddressBookController {

    /**
     * 地址簿服务层接口注入
     * 用于调用地址相关的业务逻辑
     */
    @Resource
    private AddressBookService addressBookService;

    /**
     * 新增地址接口
     * @param addressBook 地址簿对象，包含收货人、电话、详细地址等信息
     * @return 操作结果，包含是否成功及提示信息
     */
    @PostMapping()
    @Operation(summary = "新增地址簿")
    public Result<String> save(@RequestBody AddressBook addressBook) {
        // 调用服务层保存地址信息
        addressBookService.save(addressBook);
        // 返回成功结果
        return Result.success("保存成功");
    }

    /**
     * 查询用户地址列表接口
     * @return 操作结果，包含用户地址列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户地址列表")
    public Result<List<AddressBook>> list() {
        // 调用服务层查询用户地址列表
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> addressBookList = addressBookService.list(addressBook);
        // 返回成功结果，包含地址列表
        return Result.success(addressBookList);
    }

    @PutMapping
    @Operation(summary = "根据id修改地址")
    public Result<String> update(@RequestBody AddressBook addressBook) {
        // 调用服务层更新地址信息
        addressBookService.update(addressBook);
        // 返回成功结果
        return Result.success("更新成功");
    }

    /**
     * 根据id查询地址接口
     * @param id 地址簿id
     * @return 操作结果，包含地址信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        // 调用服务层根据id查询地址信息
        AddressBook addressBook = addressBookService.getById(id);
        // 返回成功结果，包含地址信息
        return Result.success(addressBook);
    }

    /**
     * 根据id删除地址接口
     * @param id 地址簿id
     * @return 操作结果，包含是否成功及提示信息
     */
    @DeleteMapping
    @Operation(summary = "根据id删除地址")
    public Result<String> delete(@RequestParam Long id) {
        addressBookService.removeById(id);
        return Result.success("删除成功");
    }

     /**
      * 根据id设置默认地址接口
      * @param addressBook 地址簿对象，包含地址簿id
      * @return 操作结果，包含是否成功及提示信息
      */
    @PutMapping("/default")
    @Operation(summary = "根据id设置默认地址")
    public Result<String> setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook);
        return Result.success("设置默认成功");
    }

     /**
      * 查询默认地址接口
      * @return 操作结果，包含默认地址信息
      */
    @GetMapping("/default")
    @Operation(summary = "查询默认地址")
    public Result<AddressBook> getDefault() {
        AddressBook addressBook = new AddressBook();
        addressBook.setIsDefault(1);
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);

        if (list != null && list.size() == 1) {
            return Result.success(list.get(0));
        }

        return Result.error("没有查询到默认地址");
    }
}