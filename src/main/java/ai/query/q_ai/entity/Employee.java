package ai.query.q_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "employee")
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "employee_id")
    private String employeeId;

    @Column
    private String name;

    @Column(name = "project_name")
    private String projectName;

    @Column
    private Date dob;

    @Column
    private String salary;

}
