-- Criar tabelas sem uuid_generate_v4() e now()
CREATE TABLE public.address (
                                id UUID NOT NULL DEFAULT RANDOM_UUID(),
                                street VARCHAR NOT NULL,
                                neighborhood VARCHAR NOT NULL,
                                number VARCHAR,
                                reference TEXT,
                                cep VARCHAR,
                                city VARCHAR NOT NULL,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                CONSTRAINT address_pkey PRIMARY KEY (id)
);

CREATE TABLE public.music_genre (
                                    id UUID NOT NULL DEFAULT RANDOM_UUID(),
                                    name VARCHAR NOT NULL UNIQUE,
                                    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                    CONSTRAINT music_genre_pkey PRIMARY KEY (id)
);

CREATE TABLE public.users (
                              id UUID NOT NULL DEFAULT RANDOM_UUID(),
                              sale_point_id UUID,
                              name VARCHAR NOT NULL,
                              email VARCHAR NOT NULL UNIQUE,
                              password VARCHAR NOT NULL,
                              documents_status VARCHAR DEFAULT 'Pending',
                              type VARCHAR NOT NULL,
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                              updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                              CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE public.sale_point (
                                   id UUID NOT NULL DEFAULT RANDOM_UUID(),
                                   admin_id UUID NOT NULL,
                                   name VARCHAR NOT NULL,
                                   address_id UUID,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                   updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                   CONSTRAINT sale_point_pkey PRIMARY KEY (id)
);

CREATE TABLE public.fair (
                             id UUID NOT NULL DEFAULT RANDOM_UUID(),
                             admin_id UUID NOT NULL,
                             thematic VARCHAR,
                             date DATE NOT NULL,
                             created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                             updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                             CONSTRAINT fair_pkey PRIMARY KEY (id)
);

CREATE TABLE public.artist (
                               id UUID NOT NULL DEFAULT RANDOM_UUID(),
                               music_genre_id UUID,
                               name VARCHAR NOT NULL,
                               banner TEXT,
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                               CONSTRAINT artist_pkey PRIMARY KEY (id)
);

CREATE TABLE public.attraction (
                                   id UUID NOT NULL DEFAULT RANDOM_UUID(),
                                   fair_id UUID NOT NULL,
                                   artist_id UUID NOT NULL,
                                   time_start TIME NOT NULL,
                                   time_end TIME NOT NULL,
                                   created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                   updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                   CONSTRAINT attraction_pkey PRIMARY KEY (id)
);

CREATE TABLE public.absence (
                                id UUID NOT NULL DEFAULT RANDOM_UUID(),
                                user_id UUID NOT NULL,
                                date DATE NOT NULL,
                                is_accepted BOOLEAN,
                                created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                CONSTRAINT absence_pkey PRIMARY KEY (id)
);

CREATE TABLE public.justification_for_absence (
                                                  id UUID NOT NULL DEFAULT RANDOM_UUID(),
                                                  absence_id UUID NOT NULL UNIQUE,
                                                  description TEXT NOT NULL,
                                                  annex BYTEA,
                                                  is_approved BOOLEAN DEFAULT FALSE,
                                                  approved_by_admin_id UUID,
                                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                                  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                                  CONSTRAINT justification_for_absence_pkey PRIMARY KEY (id)
);

CREATE TABLE public.course (
                               id UUID NOT NULL DEFAULT RANDOM_UUID(),
                               address_id UUID,
                               title VARCHAR NOT NULL,
                               description TEXT,
                               datetime TIMESTAMP WITH TIME ZONE NOT NULL,
                               created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                               updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                               CONSTRAINT course_pkey PRIMARY KEY (id)
);

CREATE TABLE public.course_presence (
                                        user_id UUID NOT NULL,
                                        course_id UUID NOT NULL,
                                        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                        CONSTRAINT course_presence_pkey PRIMARY KEY (user_id, course_id)
);

CREATE TABLE public.statement (
                                  id UUID NOT NULL DEFAULT RANDOM_UUID(),
                                  admin_id UUID NOT NULL,
                                  message TEXT NOT NULL,
                                  stereotype VARCHAR,
                                  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                  updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP(),
                                  CONSTRAINT statement_pkey PRIMARY KEY (id)
);

-- Foreign Keys
ALTER TABLE public.users ADD CONSTRAINT fk_users_sale_point FOREIGN KEY (sale_point_id) REFERENCES public.sale_point(id);
ALTER TABLE public.sale_point ADD CONSTRAINT sale_point_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.users(id);
ALTER TABLE public.sale_point ADD CONSTRAINT sale_point_address_id_fkey FOREIGN KEY (address_id) REFERENCES public.address(id);
ALTER TABLE public.fair ADD CONSTRAINT fair_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.users(id);
ALTER TABLE public.artist ADD CONSTRAINT artist_music_genre_id_fkey FOREIGN KEY (music_genre_id) REFERENCES public.music_genre(id);
ALTER TABLE public.attraction ADD CONSTRAINT attraction_fair_id_fkey FOREIGN KEY (fair_id) REFERENCES public.fair(id);
ALTER TABLE public.attraction ADD CONSTRAINT attraction_artist_id_fkey FOREIGN KEY (artist_id) REFERENCES public.artist(id);
ALTER TABLE public.absence ADD CONSTRAINT absence_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE public.justification_for_absence ADD CONSTRAINT justification_for_absence_absence_id_fkey FOREIGN KEY (absence_id) REFERENCES public.absence(id);
ALTER TABLE public.justification_for_absence ADD CONSTRAINT justification_for_absence_approved_by_admin_id_fkey FOREIGN KEY (approved_by_admin_id) REFERENCES public.users(id);
ALTER TABLE public.course ADD CONSTRAINT course_address_id_fkey FOREIGN KEY (address_id) REFERENCES public.address(id);
ALTER TABLE public.course_presence ADD CONSTRAINT course_presence_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);
ALTER TABLE public.course_presence ADD CONSTRAINT course_presence_course_id_fkey FOREIGN KEY (course_id) REFERENCES public.course(id);
ALTER TABLE public.statement ADD CONSTRAINT statement_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.users(id);