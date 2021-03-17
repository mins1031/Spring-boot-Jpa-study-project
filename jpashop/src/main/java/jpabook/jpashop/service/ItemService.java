package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemService {

    private ItemRepository itemRepository;

    @Autowired
    public void ItemService(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    @Transactional
    public void updateItem(Long itemId,String name,int price,int stockQuantity){
        Item item = itemRepository.findOne(itemId);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        //이렇게만 해도 변경감지가 일어나 값을 바꿔놓으면 알아서 커밋시 바꿔줌.
    }
    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }

}
