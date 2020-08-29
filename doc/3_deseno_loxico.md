# Deseño lóxico

Tras un análise ao deseño conceptual e en base á estrutura da súa extensión, na que as subentidades teñen relacións propias, e unha completitude total, o modelo relacional resultante sería o seguinte:

Sucursal(**_id_sucursal**, ubicacion)

Posto(**_id_posto**, descricion)

Conta(**iban**, saldo, baixa)

Persoa(**_id_persoa**, dni, nome, telefono, enderezo, data_contrato, clave)  
**_id_encargado** referencia **_id** en Persoa como clave foránea-> **UC DR**

Movemento(**iban**,**_id_movemento**, importe, concepto, data)  
**_id_conta** referencia **_id_conta** en **Conta** como clave foránea-> **UC DC**

Encarga(**_id_emp_sup**, **_id_emp_inf**, **data_ini**, data_fin)  
**_id_emp_sup** referencia **_id_persoa** en Persoa como clave foránea-> **UC DR**  
**_id_emp_inf** referencia **_id_persoa** en Persoa como clave foránea-> **UC DR**

Titular(**_id_cliente**, **_id_conta**, data_ini, data_fin)  
**_id_cliente** referencia **_id** en Persoa como clave foránea-> **UC DC**  
**_id_conta** referencia **_id** en Conta como clave foránea-> **UC DC**

Traballa(**_id_encargado**, **_id_sucursal**, **_id_posto**, **data_ini**, data_fin)  
**_id_encargado** referencia **_id_persoa** en Persoa como clave foránea-> **UC DR**  
**_id_sucursal** referencia **_id_sucursal** en Sucursal como clave foránea-> **UC DR**  
**_id_posto** referencia **_id_posto** en Posto como clave foránea-> **UC DR**  