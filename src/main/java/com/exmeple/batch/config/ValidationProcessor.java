package com.exmeple.batch.config;

import org.springframework.batch.item.ItemProcessor;

import com.exmeple.batch.model.Employee;
 
public class ValidationProcessor implements ItemProcessor<Employee,Employee>
{
    public Employee process(Employee employee) throws Exception
    {
        if (employee.getId() == null){
            System.out.println("Missing employee id : " + employee.getId());
            return null;
        }
         
        try
        {
            if(Integer.valueOf(employee.getId()) <= 0) {
                System.out.println("Invalid employee id : " + employee.getId());
                return null;
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid employee id : " + employee.getId());
            return null;
        }
        return employee;
    }
}