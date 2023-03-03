package com.iqbalaulawi.productservice.service.implementation;

import com.iqbalaulawi.productservice.entity.ProductEntity;
import com.iqbalaulawi.productservice.repository.ProductRepository;
import com.iqbalaulawi.productservice.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    public List<ProductEntity> getAll(){
        return productRepository.findAll();
    }
    @Override
    public Optional<ProductEntity> getById(Long id) {

        return productRepository.findById(id);
    }
    @Override
    public ProductEntity create(ProductEntity productEntity){
        return productRepository.save(productEntity);
    }

    @Override
    public ProductEntity update(Long id, ProductEntity productEntity) {
        Optional<ProductEntity> productEntity_update = productRepository.findById(id);
        ProductEntity update = productEntity_update.get();
        update.setName(productEntity.getName());
        update.setDescription(productEntity.getDescription());
        update.setStock(productEntity.getStock());
        update.setPrice(productEntity.getPrice());
        return productRepository.save(update);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
