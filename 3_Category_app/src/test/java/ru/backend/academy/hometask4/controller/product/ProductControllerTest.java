package ru.backend.academy.hometask4.controller.product;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public interface ProductControllerTest {

    void get_should_return_correct_list() throws Exception;
    void get_all_products_by_category_should_return_product_list() throws Exception;
    void post_should_add_product_correctly_and_return_created() throws Exception;
    void post_should_return_conflict_when_product_has_existing_id() throws Exception;
    void post_should_return_bad_request_when_product_data_incorrect() throws Exception;
    void delete_should_delete_product_and_return_no_content() throws Exception;
    void delete_should_return_not_found_when_id_is_non_existent() throws Exception;
    void put_should_update_product_correctly_and_return_accepted() throws Exception;

    void put_should_not_update_and_return_not_found_when_id_is_non_existent() throws Exception;
    void put_should_not_update_and_return_not_found_category_when_id_is_non_existent() throws Exception;
}
