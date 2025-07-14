package ai.query.q_ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@Setter
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "name")
    private String name;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "doj")
    private Date doj;

    @Column(name = "salary")
    private BigDecimal salary;

    @Column(name = "designation")
    private String designation;

    @Column(name = "chapter")
    private String chapter;

    @Column(name = "departement")
    private String departement;

    @Column(name = "bench_status")
    private boolean benchStatus;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeeProject> projects;
}
