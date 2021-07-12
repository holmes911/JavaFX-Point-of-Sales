package com.rafsan.inventory.dao;

import com.rafsan.inventory.entity.Invoice;
import com.rafsan.inventory.entity.Licence;
import javafx.collections.ObservableList;

public interface LicenceDao {
    public Licence getLicence(long id);
}
