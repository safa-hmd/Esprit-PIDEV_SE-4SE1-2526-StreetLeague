package com.example.streetleague.dto;

import com.example.streetleague.domain.Role;
import java.util.Objects;

public class UserDTO {
    private Long id;
    private String fullName;
    private Role role;
    private Long teamId;

    public UserDTO() {
    }

    public UserDTO(Long id, String fullName, Role role, Long teamId) {
        this.id = id;
        this.fullName = fullName;
        this.role = role;
        this.teamId = teamId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(fullName, userDTO.fullName) && role == userDTO.role && Objects.equals(teamId, userDTO.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, role, teamId);
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    public static class UserDTOBuilder {
        private Long id;
        private String fullName;
        private Role role;
        private Long teamId;

        public UserDTOBuilder id(Long id) { this.id = id; return this; }
        public UserDTOBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserDTOBuilder role(Role role) { this.role = role; return this; }
        public UserDTOBuilder teamId(Long teamId) { this.teamId = teamId; return this; }

        public UserDTO build() {
            return new UserDTO(id, fullName, role, teamId);
        }
    }
}
