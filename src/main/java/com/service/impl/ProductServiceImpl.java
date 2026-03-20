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
	
	@Override
	public ProductResponseDto updateProductByRating(long id, double rating) {

		Optional<Product> optionalProduct = productRepository.findById(id);
		
		if(optionalProduct.isPresent()) {
			Product product = optionalProduct.get();
			product.setRating(rating);
			Product savedProductRating = productRepository.save(product);
			
			ProductResponseDto productResponseDto = new ProductResponseDto();
			
			BeanUtils.copyProperties(savedProductRating, productResponseDto);
			
			return productResponseDto;
		}
		
		return new ProductResponseDto();
	}
	
	@Override
	public ProductResponseDto updateProductStock(long id, int stock) {

		Optional<Product> productId = productRepository.findById(id);
		
		if(productId.isPresent()) {
			Product product = productId.get();
			product.setStock(product.getStock() + stock);
			Product savedProductStock = productRepository.save(product);
			ProductResponseDto productResponseDto = new ProductResponseDto();
			BeanUtils.copyProperties(savedProductStock, productResponseDto);
			
			return productResponseDto;
		}
		
		return new ProductResponseDto();
	}
	
	@Override
	public String deleteProduct(long id) {

		Optional<Product> deleteProduct = productRepository.findById(id);
		
		if(deleteProduct.isPresent()) {
			Product product = deleteProduct.get();	
			productRepository.delete(product);
			
			return product.getProductName();
		}
		
		return "There is No Product with id: " + id;
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
