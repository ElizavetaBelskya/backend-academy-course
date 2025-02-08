package ru.backend.academy.hometask4.converter;

import org.modelmapper.ModelMapper;

public class GenericConverter<E, D> {
    private final ModelMapper mapper;
    private final Class<E> entityClass;
    private final Class<D> dtoClass;

    public GenericConverter(Class<E> entityClass, Class<D> dtoClass) {
        this.mapper = new ModelMapper();
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public D convertToDto(E entity) {
        return mapper.map(entity, dtoClass);
    }

    public E convertToEntity(D dto) {
        return mapper.map(dto, entityClass);
    }

}
