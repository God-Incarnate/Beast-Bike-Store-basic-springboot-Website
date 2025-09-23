package com.ProductBasedProject.ProductDetailsProject.Controllers;

import com.ProductBasedProject.ProductDetailsProject.Dto.ProductDto;
import com.ProductBasedProject.ProductDetailsProject.Models.Product;
import com.ProductBasedProject.ProductDetailsProject.Repositories.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsRepository productsRepository;

    @GetMapping({"","/"})
    public String showProductList(Model model){
        List<Product> products= productsRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("products",products);
        return "products/index";
    }

    @GetMapping("/create")
    public String showCreatePage(Model model){
        ProductDto productDto=new ProductDto();
        model.addAttribute("productDto",productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    //valid is used to validate format and binding result specifies whether there is validation error or not
    public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result){

        //Since we did not specify any validation parameter for imageFile in productDto thus we need to validate it manually
        if(productDto.getImageFile().isEmpty()){
            result.addError(new FieldError("productDto","imageFile","The image file is required"));
        }
        if(result.hasErrors()){
            return "products/CreateProduct";
        }
        //Saving image file if no error in upload
        MultipartFile image=productDto.getImageFile();
        Date createdAt=new Date();
        String storageFileName=createdAt.getTime()+"_"+image.getOriginalFilename();
        try{
            String uploadDir="public/";
            Path uploadPath= Paths.get(uploadDir);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            try(InputStream inputStream=image.getInputStream()){
                Files.copy(inputStream,Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e){
            System.out.println("Exception: "+e.getMessage());
        }

        Product product=new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());
        product.setCategory(productDto.getCategory());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        productsRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id){
        try{
            Product product= productsRepository.findById(id).get();
            model.addAttribute("product",product);

            ProductDto productDto=new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());
            model.addAttribute("productDto",productDto);
        }catch(Exception e){
            System.out.println("Exception: "+e.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(Model model,@RequestParam int id,@Valid @ModelAttribute ProductDto productDto,BindingResult result){

        try{
            Product product= productsRepository.findById(id).get();
            model.addAttribute("product",product);
            if(result.hasErrors()){
                return "products/EditProduct";
            }
            //if no error then check for image
            if(!productDto.getImageFile().isEmpty()){
                //delete old image
                String uploadDir="public/";
                Path oldImagePath=Paths.get(uploadDir+product.getImageFileName());
                try{
                    Files.delete(oldImagePath);
                }catch(Exception e){
                    System.out.println("Exception: "+e.getMessage());
                }
                //Save new image file
                MultipartFile image=productDto.getImageFile();
                Date createdAt=new Date();
                String storageFileName=createdAt.getTime()+"_"+image.getOriginalFilename();
                try(InputStream inputStream=image.getInputStream()){
                    Files.copy(inputStream,Paths.get(uploadDir+storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(storageFileName);
            }
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            productsRepository.save(product);
        }catch(Exception e){
            System.out.println("Exception: "+e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id){
        try{
            Product product=productsRepository.findById(id).get();
            Path imagePath=Paths.get("public/"+product.getImageFileName());
            try{
                Files.delete(imagePath);

            }catch(Exception e){
                System.out.println("Exception: "+e.getMessage());
            }
            productsRepository.delete(product);
        }catch(Exception e){
            System.out.println("Exception: "+e.getMessage());
        }
        return "redirect:/products";
    }
}
