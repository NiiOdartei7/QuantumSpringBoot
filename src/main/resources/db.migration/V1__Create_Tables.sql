create table DEPARTMENT(
                           id UUID PRIMARY KEY NOT NULL,
                           name VARCHAR(255) NOT NULL
);

create table USER_DETAILS(
         id UUID PRIMARY KEY NOT NULL,
         email VARCHAR(255) NOT NULL,
         password VARCHAR(255) NOT NULL,
         dept_id UUID,
         clearance VARCHAR(255),
         user_access_policy VARCHAR(255),
         role VARCHAR(255),
         FOREIGN KEY (dept_id) REFERENCES DEPARTMENT (id)
);

create table DOCUMENT(
    id UUID PRIMARY KEY NOT NULL ,
    user_id UUID,
    status VARCHAR(255),
    filename VARCHAR(255),
    url VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USER_DETAILS(id)
);

create table DOCUMENT_DEPARTMENT(
    department_id UUID NOT NULL,
    document_id UUID NOT NULL,
    PRIMARY KEY(department_id, document_id),
    FOREIGN KEY (department_id) REFERENCES DEPARTMENT (id),
    FOREIGN KEY (document_id) REFERENCES DOCUMENT (id)
)