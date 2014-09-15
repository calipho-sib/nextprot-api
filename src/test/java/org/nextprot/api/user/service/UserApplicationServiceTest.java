package org.nextprot.api.user.service;

import org.nextprot.api.dbunit.DBUnitBaseTest;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;

@DatabaseSetup(value = "DbXrefServiceTest.xml", type = DatabaseOperation.INSERT)
public class UserApplicationServiceTest extends DBUnitBaseTest {

}
