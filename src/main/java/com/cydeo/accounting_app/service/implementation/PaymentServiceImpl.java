package com.cydeo.accounting_app.service.implementation;

import com.cydeo.accounting_app.dto.CompanyDTO;
import com.cydeo.accounting_app.dto.PaymentDTO;
import com.cydeo.accounting_app.entity.Company;
import com.cydeo.accounting_app.entity.Payment;
import com.cydeo.accounting_app.enums.Months;
import com.cydeo.accounting_app.mapper.MapperUtil;
import com.cydeo.accounting_app.repository.PaymentRepository;
import com.cydeo.accounting_app.service.LoggedInUserService;
import com.cydeo.accounting_app.service.PaymentService;
import com.cydeo.accounting_app.service.SecurityService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl extends LoggedInUserService implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(SecurityService securityService, MapperUtil mapperUtil, PaymentRepository paymentRepository) {
        super(securityService, mapperUtil);
        this.paymentRepository = paymentRepository;
    }


    @Override
    public List<PaymentDTO> listAllPaymentsByYear(int year) {

       var loggedCompany =  mapperUtil.convert(securityService.getLoggedInUser().getCompany(), new Company());

        return  paymentRepository.findAllByCompanyAndYear(loggedCompany, year)
                .stream()
                .map(i -> mapperUtil.convert(i, new PaymentDTO()))
                .sorted(Comparator.comparing(PaymentDTO::getMonth))
                .collect(Collectors.toList());

    }

    @Override
    public void createPaymentsIfYearIsEmpty(int year) {

        var loggedCompany =  mapperUtil.convert(securityService.getLoggedInUser().getCompany(), new Company());

        var yearOfPayments = paymentRepository.findAllByCompanyAndYear(loggedCompany, year);

        if (yearOfPayments.isEmpty()){
//            iterating for each month

            for (Months months : Months.values()) {
                Payment payment = new Payment();
                payment.setMonth(months);
                payment.setYear(year);
                payment.setAmount(BigDecimal.valueOf(250));
                payment.setIsPaid(false);
                payment.setCompany(loggedCompany);

                paymentRepository.save(payment);
            }
        }

    }

}
