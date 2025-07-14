package ai.query.q_ai.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "virtual_machines")
public class VirtualMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "machine_id", unique = true, nullable = false)
    private String machineId;

    @Column(name = "machine_name", nullable = false)
    private String machineName;

    @Column(name = "configuration", nullable = false)
    private String configuration;

    @Column(name = "is_active")
    private boolean active;

}
