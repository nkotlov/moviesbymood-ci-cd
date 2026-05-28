package com.example.moviesbymood.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "file_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long fileInfoId;

    @Column(nullable = false)
    private String fileInfoFilename;

    @Column(nullable = false)
    private String fileInfoUrl;

    private String fileInfoType;

    @Column(nullable = false)
    private Instant fileInfoUploadedAt;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User fileInfoUploadedBy;
}
