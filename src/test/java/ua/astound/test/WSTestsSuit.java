package ua.astound.test;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ua.astound.test.data.User;
import ua.astound.test.utils.HttpRequestHelper;
import ua.astound.test.utils.PropertyReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestsSuit {

    @Test
    public void task1() {
        String body = HttpRequestHelper.getResponseBody(PropertyReader.INSTANCE.getPropertyValue("baseUrl"));
        List<User> users = Arrays.asList(new Gson().fromJson(body, User[].class));
        //output uncompleted titles for userID='9'
        users.stream().filter(user1 -> user1.getUserId() == 9 && !user1.isCompleted()).map(User::getTitle)
                .forEach(System.out::println);
        //check userID='1' has uncompleted tasks <= 5
        assertThat(users.stream().filter(user -> user.getUserId() == 1 && !user.isCompleted()).count())
                .isLessThanOrEqualTo(5);
    }

    @Test
    public void task2() {
        List<String> allMatches = new ArrayList<String>();
        String priceStr = PropertyReader.INSTANCE.getPropertyValue("task2");
        Matcher m = Pattern.compile("(\\d* )*\\d*\\.?\\d*\\$").matcher(priceStr);
        while (m.find()) {
            allMatches.add(m.group());
        }
        System.out.println("All matches: ".concat(allMatches.toString()));
        List<BigDecimal>
                trimmedPrices =
                allMatches.stream().map(s -> new BigDecimal(s.replaceAll("\\ |\\$", StringUtils.EMPTY)))
                        .collect(Collectors.toList());
        System.out.println("Trimmed prices: ".concat(trimmedPrices.toString()));
        List<BigDecimal>
                increasedPrices =
                trimmedPrices.stream().map(c -> c.add(new BigDecimal(1))).collect(Collectors.toList());
        System.out.println("Increased Prices: ".concat(increasedPrices.toString()));
    }

    @Test
    public void task3() {

    }
}
