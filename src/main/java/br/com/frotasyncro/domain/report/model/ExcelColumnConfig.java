package br.com.frotasyncro.domain.report.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Modelo que define a configuração de uma coluna no Excel.
 * Usado para definir estrutura de colunas dos relatórios.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelColumnConfig {

    /**
     * Título da coluna no cabeçalho do Excel
     */
    private String headerName;

    /**
     * Atributo do objeto de dados (usado em reflexão ou mapeamento)
     */
    private String attributeName;

    /**
     * Largura da coluna em caracteres
     */
    private Integer width;

    /**
     * Tipo de dados (STRING, NUMBER, DATE, CURRENCY, etc)
     */
    private String dataType;

    /**
     * Formato customizado (ex: "dd/MM/yyyy", "#,##0.00")
     */
    private String format;

    /**
     * Se deve fazer wrap de texto
     */
    @Builder.Default
    private Boolean wrapText = false;

    /**
     * Posição da coluna (ordem de exibição)
     */
    private Integer position;
}

