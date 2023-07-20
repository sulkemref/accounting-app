package com.cydeo.accounting_app.service.unit;

import com.cydeo.accounting_app.TestDocumentInitializer;
import com.cydeo.accounting_app.dto.*;
import com.cydeo.accounting_app.entity.Company;
import com.cydeo.accounting_app.entity.Invoice;
import com.cydeo.accounting_app.entity.InvoiceProduct;
import com.cydeo.accounting_app.entity.Product;
import com.cydeo.accounting_app.enums.InvoiceStatus;
import com.cydeo.accounting_app.enums.InvoiceType;
import com.cydeo.accounting_app.exception.InvoiceNotFoundException;
import com.cydeo.accounting_app.exception.ProductNotFoundException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;

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

    @Mock
    MockLoggedInUserService mockLoggedInUserService;


    @InjectMocks
    InvoiceServiceImpl invoiceService;

    class MockLoggedInUserService extends LoggedInUserService {
        public MockLoggedInUserService(SecurityService securityService, MapperUtil mapperUtil) {
            super(securityService, mapperUtil);
        }

        @Override
        public Company getCompany() {
            return new Company();
        }
    }

    @Test
    public void should_not_return_invoice_when_invoice_doesnt_exist(){
        when(invoiceRepository.findById(1L)).thenReturn(Optional.empty());

        Throwable throwable = catchThrowable(
                () -> invoiceService.findById(1L)
        );
        assertThat(throwable).isInstanceOf(InvoiceNotFoundException.class);
        assertThat(throwable).hasMessage("This Invoice with id 1 does not exist");
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
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceType(InvoiceType.SALES);

        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setQuantityInStock(9);

        InvoiceProductDTO invoiceProduct = new InvoiceProductDTO();
        invoiceProduct.setInvoice(invoiceDTO);
        invoiceProduct.setProduct(product);
        invoiceProduct.setQuantity(10);

        List<InvoiceProductDTO> allSalesInvoiceProducts = new ArrayList<>();
        allSalesInvoiceProducts.add(invoiceProduct);

        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceProductService.findAllInvoiceProductsByInvoiceId(1L)).thenReturn(allSalesInvoiceProducts);

        Throwable throwable = catchThrowable (
                () -> invoiceService.approveInvoiceById(1L)
        );
        assertThat(throwable).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    public void should_approve_sales_invoice(){
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceType(InvoiceType.SALES);
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(1L);
        invoiceDTO.setInvoiceType(InvoiceType.SALES);

        ProductDTO product = new ProductDTO();
        product.setId(1L);
        product.setQuantityInStock(10);

        InvoiceProductDTO invoiceProduct = new InvoiceProductDTO();
        invoiceProduct.setInvoice(invoiceDTO);
        invoiceProduct.setProduct(product);
        invoiceProduct.setQuantity(9);

        List<InvoiceProductDTO> allSalesInvoiceProducts = new ArrayList<>();
        allSalesInvoiceProducts.add(invoiceProduct);

        when(productService.findById(1L)).thenReturn(product);
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));
        when(invoiceProductService.findAllInvoiceProductsByInvoiceId(1L)).thenReturn(allSalesInvoiceProducts);

        invoiceService.approveInvoiceById(1L);

        InOrder inOrder = inOrder(invoiceRepository,invoiceProductService,productService,invoiceProductService);

        inOrder.verify(invoiceRepository)
                        .findById(1L);
        inOrder.verify(invoiceProductService,atLeast(3))
                .findAllInvoiceProductsByInvoiceId(1L);
        inOrder.verify(productService)
                        .findById(1L);
        inOrder.verify(productService)
                        .save(product);
        inOrder.verify(invoiceProductService)
                        .calculationProfitLossAllInvoiceProducts(1L);

        assertThat(invoice.getDate()).isEqualTo(LocalDate.now());
        assertThat(invoice.getInvoiceStatus()).isEqualTo(InvoiceStatus.APPROVED);
    }

    @Test
    public void should_create_invoice_and_put_correct_invoice_number(){
        List<String> listInvoiceNo = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            listInvoiceNo.add("P-00"+i);
        }


        when(invoiceRepository
                .findMaxInvoiceIdByType(InvoiceType.PURCHASE.getValue()
                        ,securityService.getLoggedInUser().getCompany().getId())).thenReturn(listInvoiceNo);

        InvoiceDTO invoiceDTO = invoiceService.createInvoice(InvoiceType.PURCHASE);

        assertThat(invoiceDTO.getInvoiceNo()).isEqualTo("P-010");
        assertThat(invoiceDTO.getDate()).isEqualTo(LocalDate.now());
    }
    //listInvoice

    @Test
    public void when_invoice_saved_status_type_company_should_be_assign(){
        InvoiceDTO invoiceDTO = new InvoiceDTO();

        Invoice invoice = new Invoice();

        Company company = new Company();
        company.setId(1L);

        CompanyDTO companyDTO = new CompanyDTO();

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setCompany(companyDTO);

        given(mapperUtil.convert(any(InvoiceDTO.class),any(Invoice.class))).willReturn(invoice);
        given(securityService.getLoggedInUser()).willReturn(userDTO);
        given(mockLoggedInUserService.getCompany()).willReturn(company);
        invoiceService.saveInvoiceByType(invoiceDTO,InvoiceType.PURCHASE);


        assertThat(invoice.getInvoiceType()).isNotNull();
        assertThat(invoice.getInvoiceStatus()).isNotNull();
        assertThat(invoice.getCompany()).isNotNull(); // I want to test it but I cant mock
    }

    private CompanyDTO getCurrentCompany(){
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setTitle("Current");
        companyDTO.setId(1L);

        return companyDTO;
    }

}
