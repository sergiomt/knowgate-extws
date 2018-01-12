package com.knowgate.gis.mock;

import java.io.IOException;
import java.io.InputStream;

import com.knowgate.gis.Geocoder;
import com.knowgate.gis.GeocodingException;
import com.knowgate.gis.LatLong;
import com.knowgate.gis.StreetAddress;
import com.knowgate.io.IOUtils;

public class MockGeocoder implements Geocoder {

	@Override
	public StreetAddress geoCode(String sFullAddress) throws GeocodingException {
		if (null==sFullAddress) return null;
		final String addrl = sFullAddress.toLowerCase();
		try (InputStream in = getClass().getResourceAsStream("citycountry.csv")) {
			String[] lines = IOUtils.toString(in).split("\n");
			for (String line : lines) {
				String[] components = line.split(";");
				if (addrl.indexOf(components[0].toLowerCase())>=0) {
					StreetAddress addr = new StreetAddress();
					addr.setCity(components[0]);
					addr.setCountryName(components[1]);
					addr.setCountryCode(components[2]);
					addr.setCoordinates(new LatLong(new Float(components[3]), new Float(components[4])));
					return addr;
				}
			}
		} catch (IOException e) {
			throw new GeocodingException(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public StreetAddress reverseGeoCode(float fLat, float fLng) throws GeocodingException {
		// TODO Auto-generated method stub
		return null;
	}

}
