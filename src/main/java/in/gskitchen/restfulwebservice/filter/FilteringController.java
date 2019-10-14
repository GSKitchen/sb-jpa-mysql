package in.gskitchen.restfulwebservice.filter;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class FilteringController {

    @GetMapping("my-filter")
    public MappingJacksonValue retrieveSomeBean(){
        SomeBean someBean = new SomeBean("value1", "value2", "value3");
        MappingJacksonValue mapping = new MappingJacksonValue(someBean);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1", "field3");
        FilterProvider filters = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter);
        mapping.setFilters(filters);
        return mapping;
    }

    @GetMapping("filter-list")
    public MappingJacksonValue retrieveList(){
        List<SomeBean> list = Arrays.asList(
                new SomeBean("value1", "value2", "value3"),
                new SomeBean("value11", "value12", "value13")
        );
        MappingJacksonValue mapping = new MappingJacksonValue(list);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1", "field2", "field3");
        FilterProvider filters = new SimpleFilterProvider().addFilter("SomeBeanFilter", filter);
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping("my-list")
    public MappingJacksonValue customFilter(){
        List<SomeBean> list = Arrays.asList(
                new SomeBean("vrr1", "vrr2", "vrr3"),
                new SomeBean("vrr11", "vrr12", "vrr13")
        );

        Set<String> field = new HashSet<String>();
        field.add("field1");
        field.add("field2");

        return this.getObject(list, field, "SomeBeanFilter");
    }

    private MappingJacksonValue getObject(Object object, Set<String> fieldSet, String filterName){
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(object);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept(fieldSet);
        FilterProvider filters = new SimpleFilterProvider().addFilter(filterName, filter);
        mappingJacksonValue.setFilters(filters);

        return mappingJacksonValue;
    }
}
