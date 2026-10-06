package com.generated.qualityTrace.controllers;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.generated.qualityTrace.constructors.InspectionItemResultDtoFactory;
import com.generated.qualityTrace.constructors.InspectionItemResultResponse;
import com.generated.qualityTrace.models.InspectionItemResult;
import com.generated.qualityTrace.repositories.InspectionItemResultMapper;
import com.generated.qualityTrace.routes.InspectionItemResultRoutes;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 检验项结果控制器（只读查询；检验项随检验单一并提交）。
 */
@RestController
@RequestMapping(InspectionItemResultRoutes.BASE)
public class InspectionItemResultController {

  private final InspectionItemResultMapper itemMapper;

  public InspectionItemResultController(InspectionItemResultMapper itemMapper) {
    this.itemMapper = itemMapper;
  }

  @GetMapping
  public List<InspectionItemResultResponse> list() {
    return itemMapper.selectList(new QueryWrapper<InspectionItemResult>().orderByDesc("id")).stream()
        .map(InspectionItemResultDtoFactory::from)
        .toList();
  }
}
