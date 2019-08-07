create table Questions (
    id int not null primary key,
    question text not null
);

create table Answers (
    id int not null primary key,
    question_id int not null,
    answer text not null,
    foreign key (question_id) references Questions  (id)
)