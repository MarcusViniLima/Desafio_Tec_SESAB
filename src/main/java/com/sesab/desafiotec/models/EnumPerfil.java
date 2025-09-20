/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sesab.desafiotec.models;

/**
 *
 * @author marcus
 */
public enum EnumPerfil {
    
    ADM("Adm"), USER("User");
    
    private String descricao;
    
    EnumPerfil(String descricao){
        this.descricao = descricao;
    }
    
    public String getDescricao(){
        return this.descricao;
    }
    
}
