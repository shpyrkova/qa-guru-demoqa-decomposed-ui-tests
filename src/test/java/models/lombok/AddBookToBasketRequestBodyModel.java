package models.lombok;

import lombok.Data;

import java.util.List;

@Data
public class AddBookToBasketRequestBodyModel {

    private String userId;
    private List<Isbn> collectionOfIsbns;

    @Data
    public static class Isbn {
        private String isbn;
    }

}

