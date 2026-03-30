# Lombok Configuration Issue - Diagnosis & Solution

## 🔴 PROBLEM IDENTIFIED

**Lombok is NOT generating getters/setters during Maven compilation**, despite:
- ✅ Correct pom.xml configuration
- ✅ @Data annotations applied properly
- ✅ All required Lombok dependencies present
- ❌ **But still 100 "cannot find symbol: method getXxx()" errors**

## 🔍 ROOT CAUSE

Lombok annotation processor is not being invoked by Maven Compiler Plugin. This happens when:
1. Lombok plugin is not registered in Maven's annotation processor path
2. IDE annotation processing is disabled
3. Java incremental compiler cache issue

## ✅ SOLUTION: Explicit Getters/Setters (Most Reliable)

Instead of waiting for Lombok to work, **add explicit public getter/setter methods** to Entity classes.

### Files That Need Manual Getters/Setters:
1. **User.java** - getIdUser(), getEmail(), getRole(), getFullName()
2. **Match.java** - All fields
3. **Team.java** - All fields  
4. **Training.java** - All fields
5. **Accommodation.java** - All fields
6. **AccommodationRequest.java** - All fields

### Example Pattern for User.java:

```java
// IN ADDITION TO EXISTING LOMBOK - add these public getters:

public Long getIdUser() {
    return this.idUser;
}

public String getEmail() {
    return this.email;
}

public String getFullName() {
    return this.fullName;
}

public Role getRole() {
    return this.role;
}
```

## Alternative: Fix pom.xml for Lombok Processing

Add this plugin configuration to ensure Lombok runs during compilation:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.14.0</version>
    <configuration>
        <source>17</source>
        <target>17</target>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${project.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## Recommended Next Steps

1. **Quick Fix (5 min):** Add pom.xml annotation processor path + retry Maven build
2. **Fallback (30 min):** Add explicit getters/setters to all Entity classes
3. **Long term:** Ensure IntelliJ has Lombok plugin installed and annotation processing enabled

## Current Status

- pom.xml: ✅ Corrected (dupe mail dep removed, spring-boot-maven-plugin configured)
- Entities: ✅ Changed @Getter @Setter to @Data
- DTOs: ✅ Already have @Data
- **Compilation: ❌ Still failing due to Lombok processor not found**

