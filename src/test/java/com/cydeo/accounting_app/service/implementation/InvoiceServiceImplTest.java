package com.cydeo.accounting_app.service.implementation;

import com.cydeo.accounting_app.dto.InvoiceDTO;
import com.cydeo.accounting_app.entity.Invoice;
import com.cydeo.accounting_app.exception.InvoiceNotFoundException;
import com.cydeo.accounting_app.mapper.MapperUtil;
import com.cydeo.accounting_app.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    InvoiceRepository invoiceRepository;

    @Mock
    MapperUtil mapperUtil;

    @InjectMocks
    InvoiceServiceImpl invoiceService;

    @Test
    void findInvoiceById() {
        when(invoiceRepository.findById(anyLong())).thenReturn(Optional.of(new Invoice()));
        when(mapperUtil.convert(any(Invoice.class),any(InvoiceDTO.class))).thenReturn(new InvoiceDTO());

        InvoiceDTO invoiceDTO = invoiceService.findById(anyLong());

        InOrder inOrder = inOrder(invoiceRepository,mapperUtil);

        inOrder.verify(invoiceRepository).findById(anyLong());
        inOrder.verify(mapperUtil).convert(any(Invoice.class),any(InvoiceDTO.class));

        assertNotNull(invoiceDTO);
    }

    @Test
    void indInvoiceById_ExceptionTest(){

        when(invoiceRepository.findById(anyLong())).thenThrow(new InvoiceNotFoundException("The invoice not found"));

        Throwable throwable = assertThrows(InvoiceNotFoundException.class,
                () -> invoiceRepository.findById(anyLong()));

        verify(invoiceRepository).findById(anyLong());

        verify(mapperUtil, never()).convert(any(Invoice.class),any(InvoiceDTO.class));

        assertEquals("Invoice not found", throwable.getMessage());
    }

    @Test
    void listAllInvoicesByType() {
    }

    @Test
    void saveInvoiceByType() {
    }

    @Test
    void deleteInvoiceById() {
    }

    @Test
    void approveInvoiceById() {
    }

    @Test
    void createInvoice() {
    }

    @Test
    void findLastInvoiceId() {
    }

    @Test
    void getCurrentCompany() {
    }

    @Test
    void calculateInvoice() {
    }

    @Test
    void getInvoiceForPrint() {
    }

    @Test
    void existsByClientVendorId() {
    }

    @Test
    void listAllApprovedInvoices() {
    }

    @Test
    void listAllInvoicesForDashboardChart() {
    }

    @Test
    void list3LastApprovalInvoicesForDashboard() {
    }

    @Test
    void updateInvoice() {
    }
}