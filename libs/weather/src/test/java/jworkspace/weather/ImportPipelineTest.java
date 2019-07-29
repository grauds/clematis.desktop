package jworkspace.weather;
/* ----------------------------------------------------------------------------
   Java Workspace
   Copyright (C) 2019 Anton Troshin

   This file is part of Java Workspace.

   This application is free software; you can redistribute it and/or
   modify it under the terms of the GNU Library General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.

   This application is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with this application; if not, write to the Free
   Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

   The author may be contacted at:

   anton.troshin@gmail.com
  ----------------------------------------------------------------------------
*/

import java.util.List;

import static org.testng.Assert.assertEquals;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Anton Troshin
 */
public class ImportPipelineTest {

    private static final int TEST_DATA_SIZE = 52018;

    private SessionFactory sessionFactory;

    private Session session = null;

    @Before
    public void before() {
        // setup the session factory
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(Observation.class);
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:h2:./test/resources/db/mem");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    @Test
    public void test() {

        List<Observation> result = new CsvReader(true, "27612.01.02.2005.01.02.2006.1.0.0.en.unic.00000000.csv").read();
        result.addAll(new CsvReader(true, "27612.01.02.2006.01.02.2010.1.0.0.en.unic.00000000.csv").read());
        result.addAll(new CsvReader(true, "27612.01.02.2010.01.02.2015.1.0.0.en.unic.00000000.csv").read());
        result.addAll(new CsvReader(true, "27612.01.02.2015.28.07.2019.1.0.0.en.unic.00000000.csv").read());
        assertEquals(result.size(), TEST_DATA_SIZE);

        for (Observation observation : result) {
            session.save(observation);
        }

    }

    @After
    public void after() {
        session.close();
        sessionFactory.close();
    }
}
