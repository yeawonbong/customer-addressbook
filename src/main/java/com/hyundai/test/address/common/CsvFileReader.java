package com.hyundai.test.address.common;

import com.hyundai.test.address.dao.SequenceDao;
import com.hyundai.test.address.service.AddressBookService;
import com.hyundai.test.address.util.MessageUtil;
import com.hyundai.test.address.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import com.hyundai.test.address.dao.AddressBookDao;
import com.hyundai.test.address.model.Customer;
import com.hyundai.test.address.model.Sequence;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class CsvFileReader {

    private final AddressBookDao addressBookDao;
    private final AddressBookService addressBookService;
    private final SequenceDao sequenceDao;
    private final MessageUtil messageUtil;
    private final Validator validator;
    private final ValidationUtil validationUtil;

    @PostConstruct
    public void initAddressBook() throws IOException {
        try {
            List<String> lines = read("csv/address.csv");

            boolean firstLine = true;
            Long saveCnt = 0L;
            for (String line : lines) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length != 5) {
                    log.warn("{} - {}", messageUtil.getMessage("log.csv.invalid.field"), line);
                    continue;
                }

                Customer customer = Customer.builder()
                        .id(Long.parseLong(fields[0].trim()))
                        .address(fields[1].trim())
                        .phoneNumber(fields[2].trim().replace("-", ""))
                        .email(fields[3].trim())
                        .name(fields[4].trim())
                        .build();

                // 데이터 검증
                BindingResult bindingResult = new BeanPropertyBindingResult(customer, "Customer");
                validator.validate(customer, bindingResult);
                try {
                    validationUtil.validateBindingResultOrThrow(bindingResult);
                    addressBookService.validateAllConflict(customer);
                } catch (Exception e) {
                    log.warn("{} - {}", messageUtil.getMessage("log.csv.invalid"), line);
                    continue;
                }

                addressBookDao.save(customer);
                saveCnt++;
            }
            addressBookDao.setAddressBook_init();
            log.info("{} ({}/{})", messageUtil.getMessage("log.csv.read.success"), saveCnt, lines.size() - 1);

        } catch (Exception e) {
            log.error("{}, {}", messageUtil.getMessage("log.csv.read.fail"), e.getMessage());
            throw e;
        }
    }

    @PostConstruct
    public void initSequence() {
        try {
            List<String> lines = read("csv/sequence.csv");

            boolean firstLine = true;
            for (String line : lines) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length != 2) {
                    log.warn("{}, {}", messageUtil.getMessage("log.csv.invalid.field"), line);
                    continue;
                }

                Sequence sequence = Sequence.builder()
                        .data(fields[0].trim())
                        .maxSequence(Long.parseLong(fields[1].trim()))
                        .build();

                sequenceDao.insert(sequence);
            }

            log.info(messageUtil.getMessage("log.csv.read.success"));
            log.debug("{}", sequenceDao);

        } catch (Exception e) {
            log.error("{}, {}", messageUtil.getMessage("log.csv.read.fail"), e.getMessage());
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
