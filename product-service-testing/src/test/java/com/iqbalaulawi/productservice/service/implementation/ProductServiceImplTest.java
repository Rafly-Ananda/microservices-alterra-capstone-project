package com.iqbalaulawi.productservice.service.implementation;

import com.iqbalaulawi.productservice.entity.ProductEntity;
import com.iqbalaulawi.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setup (){

    }
    @Test
    void getAllTest(){
        //arrange
        ProductEntity bookEntity1 = new ProductEntity(1L, "nama", "deskripsi", 1, 1.0);
        ProductEntity bookEntity2 = new ProductEntity(1L, "nama2", "deskripsi2", 1, 1.0);
        List<ProductEntity> expected = List.of(bookEntity1, bookEntity2);
        //
        when(productRepository.findAll()).thenReturn(expected);

        //assert
        List<ProductEntity> actual = productServiceImpl.getAll();
        Assertions.assertEquals(expected, actual);

    }
    @Test
    void getByIdTest(){
        //Inisiasi Isi dari entitynya
        ProductEntity testProduct = new ProductEntity(1L, "nama", "deskripsi", 1, 1.0);

        // act invoke make when
        // configure the mock repository to return the test product when findById is called with ID 1
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // call the service's getById method with ID 1
        Optional<ProductEntity> result = productServiceImpl.getById(1L);

        // verify that the repository's findById method was called once with ID 1
        Mockito.verify(productRepository, Mockito.times(1)).findById(1L);

        // verify that the result is equal to the test product
        assert(result.isPresent());
        assert(result.get().equals(testProduct));
    }
    @Test
    void createTest(){
        ProductEntity testProductInput = new ProductEntity(1L, "nama", "deskripsi", 1, 1.0);

        when(productRepository.save(testProductInput)).thenReturn(testProductInput);
        //di hit dulu ke function CREATE-nya
        ProductEntity testProductActual = productServiceImpl.create(testProductInput);

        //dibandingin (Assert) valid ga?
        Assertions.assertEquals(testProductInput,testProductActual);

    }
    @Test
    void updateTest(){
        //arrange
        Optional<ProductEntity> entity = Optional.of(new ProductEntity(1L, "nama", "deskripsi", 1, 1.0));
        ProductEntity entityUpdated = new ProductEntity(1L,"nama1","deskripsi1",1,1.0);

        //act
        when(productRepository.findById(any())).thenReturn(entity);
        when(productRepository.save(entityUpdated)).thenReturn(entityUpdated);

        //assert
        ProductEntity actual = productServiceImpl.update(1L,entityUpdated);
        Assertions.assertNotEquals(actual,entity);
    }
    @Test
    void deleteTest(){
        //arrange
        ProductEntity testProductInput = new ProductEntity(1L, "nama", "deskripsi", 1, 1.0);
        //act-invoke
        /*
        * Jangan make stub (when.thenReturn) di fungsi yang kembaliannya void
        * langsung aja akses fungsinya
        * */
        productServiceImpl.delete(1L);
        //cek dia udah ngehit belom itu fungsi make .verify
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(1L);

        Optional<ProductEntity> deletedProduct = productRepository.findById(1L);
        assertFalse(deletedProduct.isPresent());
    }


}
