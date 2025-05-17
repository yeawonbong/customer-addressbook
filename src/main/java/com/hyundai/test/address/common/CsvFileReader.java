package com.hyundai.test.address.common;

import com.hyundai.test.address.dao.AddressBookDao;
import com.hyundai.test.address.model.Customer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CsvFileReader {

    private final AddressBookDao addressBookDao;

    @PostConstruct
    public void initAddressBook() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("csv/success_address.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length != 4) {
                    System.out.println("⚠ 잘못된 데이터 스킵: " + line);
                    continue;
                }

                Customer customer = Customer.builder()
                        .phoneNumber(fields[0].trim())
                        .name(fields[1].trim())
                        .address(fields[2].trim())
                        .email(fields[3].trim())
                        .build();

//                if (addressBookDao.findByPhoneNumber(customer.getPhoneNumber()).isPresent()) {
//                    System.out.println("⚠ 중복 전화번호 스킵: " + customer.getPhoneNumber());
//                    continue;
//                }

                addressBookDao.insert(customer);
            }

            System.out.println("✅ CSV 데이터 메모리에 로드 완료");

        } catch (Exception e) {
            System.out.println("❌ CSV 로드 실패: " + e.getMessage());
        }
    }

    public List<String> read(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        InputStreamReader in = new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream(filename)), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(in);
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

}
