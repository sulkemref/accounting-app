package com.cydeo.accounting_app.service.unit;

import com.cydeo.accounting_app.dto.CompanyDTO;
import com.cydeo.accounting_app.dto.InvoiceDTO;
import com.cydeo.accounting_app.dto.UserDTO;
import com.cydeo.accounting_app.entity.Company;
import com.cydeo.accounting_app.entity.Invoice;
import com.cydeo.accounting_app.enums.InvoiceStatus;
import com.cydeo.accounting_app.enums.InvoiceType;
import com.cydeo.accounting_app.exception.InvoiceNotFoundException;
import com.cydeo.accounting_app.mapper.MapperUtil;
import com.cydeo.accounting_app.repository.InvoiceRepository;
import com.cydeo.accounting_app.service.InvoiceProductService;
import com.cydeo.accounting_app.service.LoggedInUserService;
import com.cydeo.accounting_app.service.ProductService;
import com.cydeo.accounting_app.service.SecurityService;
import com.cydeo.accounting_app.service.implementation.InvoiceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceImplUnitTest{

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    MapperUtil mapperUtil;

    @Mock
    SecurityService securityService;

    @Mock
    ProductService productService;

    @Mock
    InvoiceProductService invoiceProductService;

    @InjectMocks
    InvoiceServiceImpl invoiceService;

    @Test
    public void should_not_return_invoice_when_invoice_doesnt_exist(){
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> invoiceService.findById(1L)
        );
        assertThat(throwable).isInstanceOf(InvoiceNotFoundException.class);
    }

    @Test
    public void should_not_delete_invoice_when_invoice_doesnt_exist(){
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> invoiceService.deleteInvoiceById(1L)
        );
        assertThat(throwable).isInstanceOf(InvoiceNotFoundException.class);
    }

    @Test
    public void should_not_update_invoice_when_invoice_doesnt_exist(){
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> invoiceService.updateInvoice(1L,new InvoiceDTO())
        );
        assertThat(throwable).isInstanceOf(InvoiceNotFoundException.class);
    }

    @Test
    public void should_not_approve_invoice_when_invoice_doesnt_exist(){
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> invoiceService.approveInvoiceById(1L)
        );
        assertThat(throwable).isInstanceOf(InvoiceNotFoundException.class);
    }

    @Test
    public void should_approve_purchase_invoice(){
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceType(InvoiceType.PURCHASE);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        invoiceService.approveInvoiceById(1L);

        verify(invoiceRepository, atLeastOnce()).findById(1L);

        verify(invoiceProductService,atLeastOnce()).findAllInvoiceProductsByInvoiceId(1L);

        assertThat(invoice.getDate()).isEqualTo(LocalDate.now());
        assertThat(invoice.getInvoiceStatus()).isEqualTo(InvoiceStatus.APPROVED);

    }


    @Test
    public void should_not_approve_sales_invoice_because_insufficient_product_quantity(){
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceType(InvoiceType.SALES);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        invoiceService.approveInvoiceById(1L);

        verify(invoiceRepository, atLeastOnce()).findById(1L);

        verify(invoiceProductService,atLeastOnce()).findAllInvoiceProductsByInvoiceId(1L);

        assertThat(invoice.getDate()).isEqualTo(LocalDate.now());
        assertThat(invoice.getInvoiceStatus()).isEqualTo(InvoiceStatus.APPROVED);

    }


    //listInvoice

    @Test
    public void when_invoice_saved_status_type_company_should_be_assign(){
        InvoiceDTO invoiceDTO = new InvoiceDTO();

        Invoice invoice = new Invoice();

        UserDTO userDTO = new UserDTO();
//        userDTO.setCompany(new CompanyDTO());

        given(mapperUtil.convert(any(InvoiceDTO.class),any(Invoice.class))).willReturn(invoice);
        given(securityService.getLoggedInUser()).willReturn(userDTO);

        invoiceService.saveInvoiceByType(invoiceDTO,InvoiceType.PURCHASE);

        assertThat(invoice.getInvoiceType()).isNotNull();
        assertThat(invoice.getInvoiceStatus()).isNotNull();
//        assertThat(invoice.getCompany()).isNotNull();
    }

    private CompanyDTO getCurrentCompany(){
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setTitle("Current");
        companyDTO.setId(1L);

        return companyDTO;
    }

}
