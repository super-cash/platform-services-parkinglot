package cash.super_.platform.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * JPA Specific calculations with the APIs
 */
public class JpaUtil {

    /**
     * @param offset must be >= 0 indicating the initial number on the page
     * @param limit must be >= 1
     * @param sortBy which field will be sorted by
     * @return The page request based on the values provided with our defaults
     */
    public static PageRequest makePageRequest(Integer offset, Integer limit, Sort sortBy) {
        // https://www.programmersought.com/article/1459585922/
        if (offset < 0) {
            throw new IllegalArgumentException("The pagination offset must be >= 0");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("The pagination limit must be >= 1");
        }
        PageRequest pageRequest = PageRequest.of((int)(offset / limit), limit, sortBy);
        return pageRequest;
    }
}
