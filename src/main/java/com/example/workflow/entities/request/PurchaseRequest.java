package com.example.workflow.entities.request;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PurchaseRequest {
    private Long userId;
    private Long productId;

}
