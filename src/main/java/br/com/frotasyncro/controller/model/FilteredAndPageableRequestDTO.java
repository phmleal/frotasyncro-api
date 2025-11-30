package br.com.frotasyncro.controller.model;

import br.com.frotasyncro.core.specification.model.FilterCriteria;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static java.util.Objects.isNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FilteredAndPageableRequestDTO {

    @Valid
    private Pagination pagination;

    @Valid
    private List<FilterCriteria> filters;

    @JsonIgnore
    public PageRequest getPageRequest() {
        return pagination != null ?
                PageRequest.of(pagination.getPage(), pagination.getSize()) : PageRequest.of(0, 10);
    }

    public void initPagination(int page, int size) {
        if (isNull(pagination)) {
            this.pagination = new Pagination(page, size);
        } else {
            this.pagination.setPage(page);
            this.pagination.setSize(size);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Pagination {
        @JsonProperty(defaultValue = "0")
        private int page = 0;
        @JsonProperty(defaultValue = "10")
        private int size = 10;
    }

}
