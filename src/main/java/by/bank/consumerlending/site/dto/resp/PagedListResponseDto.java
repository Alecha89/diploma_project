package by.bank.consumerlending.site.dto.resp;

import java.util.List;

public record PagedListResponseDto<T>(List<T> data,
                                      long total,
                                      int page,
                                      int pageSize) {
}