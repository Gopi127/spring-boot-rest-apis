package com.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.dao.ProductRepository;
import com.dto.ProductRequestDto;
import com.dto.ProductResponseDto;
import com.model.Product;
import com.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public ProductResponseDto save(ProductRequestDto productRequestDto) {

		Product product = new Product();

		product.setProductName(productRequestDto.getProductName());
		product.setPrice(productRequestDto.getPrice());
		product.setDiscount(productRequestDto.getDiscount());
		product.setStock(productRequestDto.getStock());

		if (product.getStock() > 0) {
			product.setAvailable(true);
		}

		Product savedProduct = productRepository.save(product);

		ProductResponseDto productResponseDto = new ProductResponseDto();

		BeanUtils.copyProperties(savedProduct, productResponseDto);
		return productResponseDto;
	}
	
	@Override
	public List<ProductResponseDto> saveAllProducts(List<ProductRequestDto> productRequestDtos) {
		List<Product> products = buildProductsList(productRequestDtos);
		
		List<Product> savedProducts = productRepository.saveAll(products);
		
		List<ProductResponseDto> productsResponseList = buildProductsResponseList(savedProducts);
		
		return productsResponseList;
	}


	@Override
	public List<ProductResponseDto> getProducts() {

		List<Product> products = productRepository.findAll();

		List<ProductResponseDto> productsList = buildProductsResponseList(products);
		return productsList;
	}

	@Override
	public ProductResponseDto getProduct(long id) {
		
		Product product = productRepository.findById(id).get();
		
		ProductResponseDto productResponseDto = new ProductResponseDto();
		
		BeanUtils.copyProperties(product, productResponseDto);
		
		return productResponseDto;
	}
	
	@Override
	public List<ProductResponseDto> getProductByName(String productName) {
		List<Product> products = productRepository.findByProductNameContaining(productName);
		List<ProductResponseDto> productsResponseList = buildProductsResponseList(products);
		return productsResponseList;
	}

	
	private List<ProductResponseDto> buildProductsResponseList(List<Product> products) {
		List<ProductResponseDto> productsList = new ArrayList<>();

		for (Product product : products) {
			ProductResponseDto productResponseDto = new ProductResponseDto();

			BeanUtils.copyProperties(product, productResponseDto);

			productsList.add(productResponseDto);
		}
		return productsList;
	}
	
	private List<Product> buildProductsList(List<ProductRequestDto> productRequestDtos) {
		List<Product> products = new ArrayList<>();
		
		for (ProductRequestDto productRequest: productRequestDtos) {
			Product product = new Product();
			if(product.getStock() > 0) {
				product.setAvailable(true);
			}
			BeanUtils.copyProperties(productRequest, product);
			products.add(product);
		}
		return products;
	}

}
