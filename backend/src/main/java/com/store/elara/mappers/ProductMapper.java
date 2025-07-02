package com.store.elara.mappers;

import com.store.elara.dtos.ProductDto;
import com.store.elara.entities.Category;
import com.store.elara.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel="spring")
public interface ProductMapper {
    @Mapping(target="categoryId",source = "category.id")
    ProductDto toProductDto(Product product);

    Product toEntity(ProductDto productDto);
    @Mapping(target ="id",ignore = true)
    void updateProductDto(ProductDto productDto, @MappingTarget Product product);
}
