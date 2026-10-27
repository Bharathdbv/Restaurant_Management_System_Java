package com.rms.repository;

import com.rms.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCategory(String category);

    @Query("SELECT DISTINCT i.category FROM Item i")
    List<String> findDistinctCategories();

    List<Item> findByInameIn(List<String> names);

    Item findByIname(String iname);
}
