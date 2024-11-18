package com.example.booking_movie.utils;

import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.val;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;

import java.util.function.Supplier;

public class ElasticsearchUtil {

    //    match all query
    public static Supplier<Query> supplier() {
//        init 1 query matchAll
        return () -> Query.of(q -> q.matchAll(matchAllQuery()));
    }

    public static MatchAllQuery matchAllQuery() {
        val matchAllQuery = new MatchAllQuery.Builder();
        return matchAllQuery.build();
    }

    public static Supplier<Query> fuzzyQuerySupplier(String value) {
        return () -> Query.of(q -> q.fuzzy(fuzzyQuery(value)));
    }

    public static Supplier<Query> matchQuerySupplier(String value) {
        return () -> new MatchQuery.Builder()
                .field("name")  // Trường cần tìm kiếm
                .query(value)   // Giá trị cần tìm kiếm
                .operator(Operator.And)  // Tìm kiếm tất cả các từ trong value
                .minimumShouldMatch("100%") // Chỉ tìm kiếm khi tất cả từ khớp
                .build()._toQuery();
    }

    public static FuzzyQuery fuzzyQuery(String value) {
        // Chuyển đổi value thành chữ thường và loại bỏ dấu
        String normalizedValue = removeDiacritics(value.toLowerCase());

        // Khởi tạo fuzzy query builder
        FuzzyQuery.Builder fuzzyQueryBuilder = new FuzzyQuery.Builder();

        // Thêm giá trị cần tìm kiếm cho trường 'name' và giá trị fuzzy
        fuzzyQueryBuilder.value(normalizedValue);  // Giá trị cần tìm kiếm
        fuzzyQueryBuilder.field("name"); // Trường cần tìm kiếm

        // Thêm tham số fuzziness cho phép tìm kiếm gần đúng
        fuzzyQueryBuilder.fuzziness("AUTO");

        // Trả về đối tượng FuzzyQuery đã được xây dựng
        return fuzzyQueryBuilder.build();
    }

    // Hàm loại bỏ dấu tiếng Việt
    public static String removeDiacritics(String input) {
        if (input == null) {
            return null;
        }
        // Loại bỏ dấu tiếng Việt và chuyển tất cả thành chữ thường
        return input.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũûừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[\u0111]", "d")  // Loại bỏ dấu đ
                .toLowerCase();  // Đảm bảo tất cả chữ cái đều là chữ thường
    }

//    public static Supplier<Query> fuzzyQueySupplier(String value) {
//        return () -> Query.of(q -> q.bool(b -> b
//                .should(prefixQuery(value))    // Tìm kiếm với prefix query
//                .should(fuzzyQuery(value))     // Tìm kiếm với fuzzy query
//                .should(wildcardQuery(value))  // Tìm kiếm với wildcard query
//        ));
//    }
//
//    public static Query prefixQuery(String value) {
//        // Tìm kiếm với tiền tố
//        return Query.of(q -> q.prefix(p -> p.field("name").value(value)));
//    }
//
//    public static Query fuzzyQuery(String value) {
//        // Tìm kiếm gần giống với fuzziness AUTO
//        return Query.of(q -> q.fuzzy(f -> f.field("name").value(value).fuzziness("AUTO").prefixLength(1)));
//    }
//
//    public static Query wildcardQuery(String value) {
//        // Tìm kiếm với wildcard (tất cả các từ bắt đầu với value)
//        return Query.of(q -> q.wildcard(w -> w.field("name").value(value + "*")));
//    }

}
