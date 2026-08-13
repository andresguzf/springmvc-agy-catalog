package com.andres.course.agy.springboot.springmvc.app.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

public class CheckoutForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido del cliente es obligatorio")
    private String lastName;

    @NotBlank(message = "El RUT / Identificador fiscal es obligatorio")
    private String rut;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Ingresa un correo electrónico válido")
    private String email;

    @NotBlank(message = "El teléfono de contacto es obligatorio")
    private String phone;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    private String city;

    @NotBlank(message = "Selecciona un método de envío")
    private String shippingMethod = "ENVIO_ESTANDAR";

    @NotBlank(message = "Selecciona un método de pago")
    private String paymentMethod = "TARJETA";

    public CheckoutForm() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
