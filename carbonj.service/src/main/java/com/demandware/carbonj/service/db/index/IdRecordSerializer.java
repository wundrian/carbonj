/**
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.demandware.carbonj.service.db.index;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import static java.nio.charset.StandardCharsets.UTF_8;

class IdRecordSerializer
                implements RecordSerializer<Long, IdRecord>
{
    private boolean longId;
    public IdRecordSerializer()
    {
        this(false);
    }

    public IdRecordSerializer(boolean longId)
    {
        this.longId = longId;
    }

    @Override
    public Long key( byte[] keyBytes )
    {
        if(longId)
        {
            return Longs.fromByteArray( keyBytes );
        }
        Integer intKey = Ints.fromByteArray(keyBytes);
        return intKey.longValue();
    }

    @Override
    public IdRecord toIndexEntry( byte[] keyBytes, byte[] valueBytes)
    {
        Long key = key(keyBytes);
        return toIndexEntry( key, valueBytes);
    }

    @Override
    public IdRecord toIndexEntry( Long key, byte[] valueBytes)
    {
        ByteArrayDataInput in = ByteStreams.newDataInput( valueBytes );
        // a byte for versioning
        byte entryType = in.readByte();
        return new IdRecord( key, in.readUTF() );
    }

    @Override
    public byte[] keyBytes(Long key)
    {
        if(longId) {
            return Longs.toByteArray(key);
        }
        return Ints.toByteArray(key.intValue());
    }

    @Override
    public byte[] valueBytes(IdRecord e)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        // leaving a byte for versioning
        out.writeByte(0);
        out.writeUTF(e.metricName());
        return out.toByteArray();
    }

}
