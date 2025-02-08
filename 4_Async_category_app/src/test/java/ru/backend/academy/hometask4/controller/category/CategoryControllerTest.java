package ru.backend.academy.hometask4.controller.category;

public interface CategoryControllerTest {
    void get_should_return_list_of_categories_with_correct_length() throws Exception;
    void get_should_return_list_with_default_category() throws Exception;
    void post_should_return_added_category() throws Exception;

    void post_should_return_added_category_with_transliterated_title() throws Exception;

    void post_should_add_category_with_same_url_but_another_title() throws Exception;

    void post_should_not_add_category_with_same_url_and_same_title() throws Exception;

    void put_should_update_category_with_same_url_but_another_title() throws Exception;
    void put_should_not_update_category_with_same_url_and_same_title() throws Exception;

    void put_should_not_update_unknown_id_and_return_not_found() throws Exception;
    void delete_should_delete_category_and_return_no_content() throws Exception;

    void delete_should_return_not_found_when_id_is_unknown() throws Exception;
}
