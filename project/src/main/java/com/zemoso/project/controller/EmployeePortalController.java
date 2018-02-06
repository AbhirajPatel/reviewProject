package com.zemoso.project.controller;

import com.zemoso.project.exception.DbException;
import com.zemoso.project.exception.MapperException;
import com.zemoso.project.model.Employee;
import com.zemoso.project.service.EmployeePortalService;
import com.zemoso.project.utils.CompanyUtil;
import com.zemoso.project.utils.Constant;
import com.zemoso.project.utils.EmployeeMapper;
import com.zemoso.project.utils.FileSaveMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeePortalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeePortalController.class);


    @Autowired
    private EmployeePortalService employeePortalService;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private FileSaveMapper fileSaveMapper;

    /**
     * rest controller to get all employee data from db;
     * @return <Map<String, List<Map<String, Object>>>>
     */
   @RequestMapping(method= RequestMethod.GET)
   public ResponseEntity getAllEmployeeOfCompany(){
       Long companyId = CompanyUtil.getCompanyId();
       Map<String,List<Map<String,Object>>> responseMap = new HashMap<>();
       List<Map<String, Object>> mapList = new ArrayList<>();
       try{
               List<Employee> employees = employeePortalService.getAllEmployee(companyId);
           employees.forEach(item->{
               Map<String, Object> employeeMap = null;
               try {
                   employeeMap = employeeMapper.getObjectMap(item);
               } catch (MapperException e) {
                   LOGGER.error(e.getMessage() , e);
               }
               mapList.add(employeeMap);
       });
       }
       catch (Exception e){
           LOGGER.error(e.getMessage() , e);
           return ResponseEntity.status(HttpStatus.FORBIDDEN).
                   body(e.getMessage());
       }


       responseMap.put("employees" , mapList);
       return ResponseEntity.ok().body(responseMap);

    }
    /**
     * Rest controller to get selected employee
     *
     * @param employeeId
     * @return <Map<String, Object>>
     */
    @RequestMapping(path = "/{employeeId}", method = RequestMethod.GET)
    public ResponseEntity getProjectsById(@PathVariable Long employeeId) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Employee employee = employeePortalService.getEmployee(employeeId);
                responseMap.put(Constant.EMPLOYEE, employeeMapper.getObjectMap(employee));
            return ResponseEntity.ok().body(responseMap);
        }catch (Exception e){
            LOGGER.error(e.getMessage() , e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).
                    body(e.getMessage());
        }
    }
    /**
     * REST controller to add employee in db
     * @return type <Map<String, Object>>
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addEmployee(@RequestBody Map<String , Map> map){
        Map<String, Object> employeeMap = map.get(Constant.EMPLOYEE);
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Employee employee = employeeMapper.getMapObject(employeeMap);
            employeePortalService.save(employee);
            Map<String, Object> emap = employeeMapper.getObjectMap(employee);
            responseMap.put(Constant.EMPLOYEE, emap);
            return ResponseEntity.ok().body(responseMap);
        }catch (Exception e){
            LOGGER.error(e.getMessage() , e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).
                    body(e.getMessage());
        }
    }

    //TODO
    //write controller for the update the data

    /**
     * REST controller to update/edit a particular employee data
     *@return <Map<String,Object>>
     */
   @RequestMapping(path = "/{employeeId}", method = RequestMethod.PUT)
    public ResponseEntity updateEmployee
   (@RequestBody Map<String ,Map> map , @PathVariable Long employeeId){

       try {

           Employee ipEmployee = employeeMapper.getMapObject(map.get(Constant.EMPLOYEE));
           Employee dbEmployee = employeePortalService.getEmployee(employeeId);
           dbEmployee.setFirstName(ipEmployee.getFirstName());
           dbEmployee.setMiddleName(ipEmployee.getMiddleName());
           dbEmployee.setLastName(ipEmployee.getLastName());
           dbEmployee.setBiodata(ipEmployee.getBiodata());
           dbEmployee.setSkill(ipEmployee.getSkill());
           dbEmployee.setEmail(ipEmployee.getEmail());
           dbEmployee.setMobileNo(ipEmployee.getMobileNo());
           dbEmployee.setDepartment(ipEmployee.getDepartment());
           dbEmployee.setProject(ipEmployee.getProject());
           try {
               Map<String, Object> employeeRoleMap = (Map<String, Object>) map.get(Constant.EMPLOYEE).get(Constant.EMPLOYEE_ROLE);
               dbEmployee.setEmployeeRole(employeeRoleMap.get(Constant.NAME).toString());
           }catch (Exception e){ dbEmployee.setReportingEmployeeName(ipEmployee.getReportingEmployeeName());}
           dbEmployee.setLocation(ipEmployee.getLocation());
           dbEmployee.setProfilePic(ipEmployee.getProfilePic());
           dbEmployee.setStartDate(ipEmployee.getStartDate());
           dbEmployee.setReportingEmployeeName(ipEmployee.getReportingEmployeeName());
           employeePortalService.save(dbEmployee);

           Map<String, Object> responseMap = new HashMap<>();
               responseMap.put(Constant.EMPLOYEE, employeeMapper.getObjectMap(dbEmployee));

           }
       catch (Exception e){
           LOGGER.error(e.getMessage(),e);
           ResponseEntity.status(HttpStatus.FORBIDDEN).
                   body(e.getMessage());
       }
       return ResponseEntity.ok().body(map);
   }

}