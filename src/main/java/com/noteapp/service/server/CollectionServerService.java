package com.noteapp.service.server;

import com.noteapp.model.datatransfer.BaseDataTransferModel;
import java.util.List;

/**
 *
 * @author admin
 */
public interface CollectionServerService<T extends BaseDataTransferModel> extends ServerService<List<T>> {
    
}
