alter table writing_exercises
    add content varchar(255) not null;

alter table writing_exercises
    add image varchar(255) null;

drop table writing_exercise_detail;