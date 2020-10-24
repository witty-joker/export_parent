package com.itheima.web.controller.cargo;

import cn.hutool.core.lang.UUID;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.itheima.domain.cargo.*;
import com.itheima.domain.cargo.ExtCproduct;
import com.itheima.domain.cargo.ExtCproductExample;
import com.itheima.service.cargo.ExtCproductService;
import com.itheima.service.cargo.ExtCproductService;
import com.itheima.service.cargo.FactoryService;
import com.itheima.utils.FileUploadUtil;
import com.itheima.web.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

// extCproduct 附件管理视图 ExtCproductController
@Controller
@RequestMapping("/cargo/extCproduct")
public class ExtCproductController extends BaseController {

    @Reference
    private ExtCproductService extCproductService;

    @Reference
    private FactoryService factoryService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    // 分页查询
    @RequestMapping(value = "/list", name = "附件列表查询")
    public String list(
            @RequestParam(defaultValue = "1", name = "page") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            String contractId,
            String contractProductId
    ){
        /*
            附件查看分析：
                1 查询
         */
        // 1 获取货物的ContractProductExample
        ExtCproductExample extCproductExample = new ExtCproductExample();

        // 2 查询当前企业id、合同id下,  的所有货物信息
        ExtCproductExample.Criteria criteria = extCproductExample.createCriteria();
        criteria.andContractIdEqualTo(contractId);  // 合同id
        criteria.andContractProductIdEqualTo(contractProductId);    // 合同货物id

        PageInfo pageInfo = extCproductService.findByPage(pageNum, pageSize, extCproductExample);

        // 3 保存到集合page中 查询的是所有附件信息
        request.setAttribute("page", pageInfo);
        System.out.println(pageInfo);

        // 4 查询所有生产附件的 【厂家列表】
        FactoryExample factoryExample = new FactoryExample();
        // 查询生产附件的厂家
        factoryExample.createCriteria().andCtypeEqualTo("附件");

        List<Factory> factoryList = factoryService.findAll(factoryExample);

        request.setAttribute("factoryList", factoryList);

        // 5 回传contractId
        request.setAttribute("contractId", contractId);
        request.setAttribute("contractProductId", contractProductId);

        // 6 跳转页面
        return "/cargo/extc/extc-list";
    }


    // 跳转编辑附件内容
    @RequestMapping(value = "/toUpdate", name = "编辑附件内容")
    public String toUpdate(String id, String contractId) {
        /*
            编辑附件内容分析：
                1 根据id获取附件内容
                2 将附件内容回显到页面上
                3 查询所有生产附件的厂家列表
                4 跳转到修改页面
         */
        // 1 根据id获取附件内容
        ExtCproduct extCproduct = extCproductService.findById(id);

        // 2 将附件内容回显到页面
        request.setAttribute("extCproduct", extCproduct);

        // 3 查询所欲生产附件的厂家列表
        FactoryExample factoryExample = new FactoryExample();

        FactoryExample.Criteria criteria = factoryExample.createCriteria().andCtypeEqualTo("附件");

        List<Factory> factoryList = factoryService.findAll(factoryExample);

        request.setAttribute("factoryList", factoryList);

        // 3 跳转到附件编辑页面
        return "/cargo/extc/extc-update";
    }


    // 新增/修改共同使用
    @RequestMapping(value = "/edit", name = "新增/修改共同使用")
    public String edit(ExtCproduct extCproduct, MultipartFile productPhoto) {
        //TODO 修改时不修改图片会出现问题。

        // 添加文件上传代码
        try {
            String upload = fileUploadUtil.upload(productPhoto);
            extCproduct.setProductImage(upload);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (extCproduct.getFactoryName().equals("")) {
            return "redirect:/cargo/extCproduct/list.do?contractId=" + extCproduct.getContractId() + "&contractProductId=" + extCproduct.getContractProductId();
        }

        // 判断extCproduct中id是否为空，为空就新增
        if (StringUtils.isEmpty(extCproduct.getId())) {
            // 1 添加主键
            extCproduct.setId(UUID.randomUUID().toString());

            // 2. 设置附件信息
            extCproduct.setCompanyId(getUser().getCompanyId());
            extCproduct.setCompanyName(getUser().getCompanyName());

            // 4 调用添加方法
            extCproductService.save(extCproduct);
        } else {
            // 修改
            // 调用修改update
            extCproductService.update(extCproduct);
        }

        // 共：重定向到list页面
        return "redirect:/cargo/extCproduct/list.do?contractId=" + extCproduct.getContractId() + "&contractProductId=" + extCproduct.getContractProductId();
    }


    // 删除附件信息 根据id进行删除
    @RequestMapping(value = "/delete", name = "删除附件信息")
    public String delete(String id, String contractId, String contractProductId) {
        // 根据id查询删除
        extCproductService.delete(id);

        return "redirect:/cargo/extCproduct/list.do?contractId=" + contractId + "&contractProductId=" + contractProductId;

    }

}
